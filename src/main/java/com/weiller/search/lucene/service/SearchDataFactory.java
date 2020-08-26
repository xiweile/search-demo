package com.weiller.search.lucene.service;

import java.util.List;

public interface SearchDataFactory<T> {

    List<T> getData();
}
