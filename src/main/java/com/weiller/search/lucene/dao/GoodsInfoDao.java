package com.weiller.search.lucene.dao;

import com.weiller.search.lucene.model.GoodsInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsInfoDao {

    List<GoodsInfo> findAll();
}
