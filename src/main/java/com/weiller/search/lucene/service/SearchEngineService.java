package com.weiller.search.lucene.service;

import com.weiller.search.lucene.model.SearchResult;

public interface SearchEngineService  {

    <T>  void createIndex(SearchDataFactory<T>  dataFactory);

    SearchResult searchIndex(String[] queryFields,String keyword, int pageIndex, int pageSize);

    String[] checkWords(String field,String words);
}
