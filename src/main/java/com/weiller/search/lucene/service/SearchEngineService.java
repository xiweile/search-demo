package com.weiller.search.lucene.service;

import com.weiller.search.lucene.model.SearchResult;

public interface SearchEngineService  {

    void createIndex(SearchDataFactory<?> dataFactory);

    SearchResult searchIndex(String[] queryFields,String keyword, int pageIndex, int pageSize);
}
