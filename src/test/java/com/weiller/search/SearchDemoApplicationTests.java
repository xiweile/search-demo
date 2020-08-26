package com.weiller.search;

import com.weiller.search.lucene.dao.NsrxxDao;
import com.weiller.search.lucene.model.Nsrxx;
import com.weiller.search.lucene.model.SearchResult;
import com.weiller.search.lucene.service.SearchEngineService;
import com.weiller.search.lucene.service.impl.MySearchDataFactory;
import com.weiller.search.lucene.service.impl.SearchEngineServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
class SearchDemoApplicationTests {


	@Autowired
	NsrxxDao nsrxxDao;

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
		String[] queryFields = {"nsrsbh", "nsrmc", "fddbrxm"};
		SearchResult result = searchEngineService.searchIndex(queryFields,"陕西 李选红", 1, 10);
		List<Map<String, Object>> records = result.getRecords();
		Long total = (Long)result.getTotal();

		System.out.println("总匹配条数："+total);
		records.forEach(System.out::println);
	}

}
