package com.weiller.search.lucene.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchResult {

    private List<Map<String, Object>> records;

    private Long total;
}
