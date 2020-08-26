package com.weiller.search.lucene.annotation;

public enum FiledType {

    /** 不分词 + 可索引  + 存储（可选）*/
    STRING,
    /** 不分词 + 不索引 + 可存储*/
    STORED,
    /** 可分词 + 可索引 + 存储（可选）*/
    TEXT,
    /** 可分词 + 可索引 + 存储（可选）*/
    LONG;

    FiledType(){}
}
