# SEARCH-DEMO搜索案例

### 案例一：基于Lucene开发的简易版搜索工具

功能: 全文检索、ik分词器、匹配高亮
   

使用步骤：

    1.定义需要建立索引的实体类
    2.定义泛型为索引实体的搜索数据工厂实现类
    3.创建索引：调用SearchEngineService.createIndex(mySearchDataFactory)
    4.搜索：调用SearchEngineService.searchIndex(查询字段数组,关键词, 当前页码, 每页展示数)
测试案例:
    
``` java
@RunWith(SpringRunner.class)
@SpringBootTest
class SearchDemoApplicationTests {

	@Autowired
	MySearchDataFactory mySearchDataFactory;

	@Autowired
	SearchEngineService searchEngineService;

	@Test
	void createIndex() {
		searchEngineService.createIndex(mySearchDataFactory);
	}

	@Test
	void searchData() {
		String[] queryFields = {"goodsName", "goodsIntro" };
		SearchResult result = searchEngineService.searchIndex(queryFields,"小米8 全网通", 1, 10);
		List<Map<String, Object>> records = result.getRecords();
		Long total = (Long)result.getTotal();

		System.out.println("总匹配条数："+total);
		records.forEach(System.out::println);
	}

}


```