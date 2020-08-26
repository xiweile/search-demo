package com.weiller.search.lucene.service.impl;

import com.weiller.search.lucene.dao.NsrxxDao;
import com.weiller.search.lucene.model.Nsrxx;
import com.weiller.search.lucene.service.SearchDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MySearchDataFactory implements SearchDataFactory<Nsrxx> {

    @Autowired
    private NsrxxDao nsrxxDao;

    @Override
    public List<Nsrxx> getData() {
        return nsrxxDao.findAll();
    }
}
