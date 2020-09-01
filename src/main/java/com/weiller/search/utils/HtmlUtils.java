package com.weiller.search.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.List;

public class HtmlUtils {
    private static void extractText(Node node,StringBuffer buffer){
        /* TextNode直接返回结果 */
        if(node instanceof TextNode){
            buffer.append(((TextNode) node).text());
            return ;
        }   
        /* 非TextNode的Node，遍历其孩子Node */
        List<Node> children = node.childNodes();   

        for (Node child: children) {   
             extractText(child,buffer) ;
        }   
    }
    /* 使用jsoup解析html并转化为提取字符串*/
    public static String html2Str(String html){
        StringBuffer buffer = new StringBuffer("");
        Document doc = Jsoup.parse(html);
        extractText(doc,buffer);
        return buffer.toString().trim();
    }
}