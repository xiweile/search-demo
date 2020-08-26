package com.weiller.search.lucene.model;

import com.weiller.search.lucene.annotation.FiledType;
import com.weiller.search.lucene.annotation.SearchField;
import lombok.Data;

@Data
public class GoodsInfo {

    @SearchField(name = "goodsId",type = FiledType.STRING )
    private String goodsId;

    @SearchField(name = "goodsName",type = FiledType.TEXT )
    private String goodsName;

    @SearchField(name = "goodsIntro",type = FiledType.TEXT )
    private String goodsIntro;

    @SearchField(name = "sellingPrice",type = FiledType.STRING)
    private String sellingPrice;

    @SearchField(name = "stockNum",type = FiledType.STRING)
    private String stockNum;

    @SearchField(name = "goodsCoverImg",type = FiledType.STRING,stored = false)
    private String goodsCoverImg;
}
