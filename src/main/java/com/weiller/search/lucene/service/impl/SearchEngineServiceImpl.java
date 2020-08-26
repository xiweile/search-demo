package com.weiller.search.lucene.service.impl;


import com.weiller.search.lucene.annotation.FiledType;
import com.weiller.search.lucene.annotation.SearchField;
import com.weiller.search.lucene.model.SearchResult;
import com.weiller.search.lucene.service.SearchDataFactory;
import com.weiller.search.lucene.service.SearchEngineService;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Field.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SearchEngineServiceImpl implements SearchEngineService  {

    //分词器
    private Analyzer analyzer = new IKAnalyzer(true);

    //索引库的存储目录
    private Directory directory ;
    //索引文件读取器
    private volatile DirectoryReader ireader;

    //索引搜索器
    private volatile IndexSearcher isSearcher;

    public SearchEngineServiceImpl(@Value("${lucene.index-dir}") String indexDir){
        try {
            File file = new File(indexDir);
            if(!file.exists()){
                file.mkdirs();
            }
            log.info("创建 Directory 实例");
            this.directory = FSDirectory.open(new File(indexDir).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public  <T> void createIndex(SearchDataFactory<T>  dataFactory) {
        Class<?> dataClass = this.parseFactoryTypeClass(dataFactory.getClass());
        Map<String, SearchField> searchFiledMap = this.parseSearchFiled(dataClass);
        List<T> datas = dataFactory.getData();

        log.info("开始创建索引 总{}条", datas.size() );

        long startTime = System.currentTimeMillis();

        // 定义索引操作对象
        IndexWriter indexWriter=null;

        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);

         try {
            indexWriter = new IndexWriter(directory, indexWriterConfig);
            indexWriter.deleteAll();

            //创建字段
            for (int i=0;i< datas.size();i++){
                T data =  datas.get(i);
                //创建文档对象
                Document document=new Document();
                Field[] declaredFields2 = data.getClass().getDeclaredFields();
                for (Field field: declaredFields2){
                    field.setAccessible(true);
                    String name = field.getName();
                    SearchField searchField = searchFiledMap.get(name);
                    org.apache.lucene.document.Field luceneFiled = null;

                    String filedName = "".equals(searchField.name())?name: searchField.name();
                    FiledType filedType = searchField.type();
                   // boolean indexed = searchField.indexed();
                    boolean stored = searchField.stored();
                    switch (filedType){
                        case STRING:
                            luceneFiled = new StringField(filedName,(String)field.get(data),stored? Store.YES:Store.NO);
                            break;
                        case TEXT:
                            luceneFiled = new TextField(filedName,(String)field.get(data),stored? Store.YES:Store.NO);
                            break;
                        case LONG:
                            luceneFiled = new LongPoint(filedName,(Long) field.get(data) );
                            break;
                        case STORED:
                            luceneFiled = new StoredField(filedName,(Long) field.get(data) );
                            break;
                        default:

                    }
                    document.add(luceneFiled);
                }
                //把文档对象写入索引库
                indexWriter.addDocument(document);
            }
            double time =  (System.currentTimeMillis()-startTime)/1000.0;
            log.info("开始创建完成 成功数 {} ，耗时 {}s " ,datas.size(),Math.round(time));
        } catch (Exception e) {
            log.error("创建索引发生异常",e);
        } finally {
            if(indexWriter!=null){
                //提交
                try {
                    indexWriter.commit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 索引操作流关闭
                try {
                    indexWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public SearchResult  searchIndex(String[] queryFields ,String keyword,int pageIndex,int pageSize) {
        //刷新搜索器
        refreshSearcher();

        SearchResult result = new SearchResult();
        try {

            int start = (pageIndex - 1) * pageSize;
            int end = pageSize * pageIndex;

            //获取搜索的结果，指定返回document返回的个数
            QueryParser queryParser = new MultiFieldQueryParser(queryFields,analyzer);
            Query query = queryParser.parse(keyword);
            log.info("查询关键词：{}",keyword);
            log.info("解析后查询语句：{}",query.toString());
            //最终被分词后添加的前缀和后缀处理器
            SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<em>","</em>");
            //高亮搜索的词添加到高亮处理器中
            Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));

            if (isSearcher==null){//如果无可用索引
                result.setTotal(0L);
                result.setRecords(new ArrayList<>());
                return result;
            }

            TopDocs topDocs = isSearcher.search(query, end);
            TotalHits totalHits = topDocs.totalHits;
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            if (end > scoreDocs.length) {
                end = scoreDocs.length;
            }

            List<Map<String,Object>> records = new ArrayList<>();

            for (int i = start; i < end; i++) {
                Map<String,Object> map = new HashMap<>();
                Document targetDoc = isSearcher.doc(scoreDocs[i].doc);
                List<IndexableField> fields = targetDoc.getFields();
                for (IndexableField indexableField:fields){
                    map.put(indexableField.name(), indexableField.stringValue());

                    TokenStream tokenStream = TokenSources.getTokenStream(isSearcher.getIndexReader(), scoreDocs[i].doc,indexableField.name(),analyzer);
                    TextFragment[] bestTextFragments = highlighter.getBestTextFragments(tokenStream, indexableField.stringValue(), false, 10);
                    for (TextFragment textFragment:bestTextFragments){
                        if ( textFragment!= null  &&  textFragment.getScore() > 0)  {
                            map.put(indexableField.name(),textFragment.toString() ) ;
                        }
                    }
                }

                records.add(map);
            }
            result.setRecords(records);
            result.setTotal( totalHits.value);

        } catch ( Exception e) {
            e.printStackTrace();
        }
        return result;
    }

     /**
     * 刷新 IndexReader
      */
     private void refreshSearcher() {
         synchronized (this){
             try {
                 if(ireader==null) {
                     log.info("创建 DirectoryReader 实例");
                     ireader = DirectoryReader.open(directory);
                     isSearcher = new IndexSearcher(ireader);
                 } else {
                     // 如果 IndexReader 不为空，就使用 DirectoryReader 打开一个索引变更过的 IndexReader 类
                     // 此时要记得把旧的索引对象关闭
                     DirectoryReader dr = DirectoryReader.openIfChanged((DirectoryReader)ireader);
                     if(dr!=null) {
                         try {
                             ireader.close();
                         }catch (IOException e){
                             log.error("关闭索引读取流失败",e);
                         }
                         log.info("索引变更，更新 DirectoryReader 实例");
                         ireader = dr;
                         isSearcher = new IndexSearcher(ireader);
                     }
                 }

             } catch (IndexNotFoundException e1){
                 log.error("无索引文件异常",e1);
             } catch (CorruptIndexException e2) {
                 log.error("查询索引异常",e2);
             } catch (IOException e3) {
                 log.error("查询索引异常",e3);
             }

         }
    }


    @PreDestroy
    private void close(){

         if (ireader!=null){
             log.info("释放 DirectoryReader 资源");
             try {
                 ireader.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
         if (directory!=null){
             log.info("释放 Directory 资源");
             try {
                 directory.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
    }

    /**
     * 解析SearchField信息
     * @param dataClass 该实体类
     * @return  name属性和注解实例对照关系 map
     */
    private Map<String, SearchField> parseSearchFiled(Class<?> dataClass){
        Map<String, SearchField> map = new HashMap<>();
        Field[] declaredFields = dataClass.getDeclaredFields();
        for (Field field: declaredFields){
            field.setAccessible(true);
            String name = field.getName();
            Annotation[] annotations = field.getDeclaredAnnotations();
            SearchField searchField = null;
            for (Annotation annotation : annotations){
                Class<? extends Annotation> aClass = annotation.annotationType();
                String simpleName = aClass.getSimpleName();
                if(simpleName.equals(SearchField.class.getSimpleName())){
                    searchField = (SearchField)annotation;
                }
            }

            map.put(name, searchField);

        }
        return map;
    }

    /**
     * 解析类注解实际类型
     * @param clazz 当前实例类
     * @return 注解类
     */
    private Class<?> parseFactoryTypeClass(Class<?> clazz){
        ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericInterfaces()[0];
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Type actualTypeArgument = actualTypeArguments[0];
        Class<?> dataClass = null;
        if (actualTypeArgument instanceof Class<?>) {
            dataClass = (Class<?> )actualTypeArgument;
        }

        return dataClass;
    }
}
