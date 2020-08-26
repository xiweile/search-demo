package com.weiller.search;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.weiller.search.*.dao")
public class SearchDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchDemoApplication.class, args);
	}

}
