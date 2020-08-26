package com.weiller.search.lucene.service.impl;

import com.weiller.search.lucene.dao.GoodsInfoDao;
import com.weiller.search.lucene.model.GoodsInfo;
import com.weiller.search.lucene.service.SearchDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MySearchDataFactory implements SearchDataFactory<GoodsInfo> {

    @Autowired
    private GoodsInfoDao goodsInfoDao;

    @Override
    public List<GoodsInfo> getData() {
        return goodsInfoDao.findAll();
    }
}
