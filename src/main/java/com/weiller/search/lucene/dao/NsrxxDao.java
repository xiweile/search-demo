package com.weiller.search.lucene.dao;

import com.weiller.search.lucene.model.Nsrxx ;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NsrxxDao {

    List<Nsrxx> findAll();
}
