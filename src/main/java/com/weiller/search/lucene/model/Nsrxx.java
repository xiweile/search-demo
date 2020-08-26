package com.weiller.search.lucene.model;

import com.weiller.search.lucene.annotation.FiledType;
import com.weiller.search.lucene.annotation.SearchField;
import lombok.Data;

@Data
public class Nsrxx {

    @SearchField(name = "djxh",type = FiledType.STRING )
    private String djxh;

    @SearchField(name = "nsrsbh",type = FiledType.STRING )
    private String nsrsbh;

    @SearchField(name = "nsrmc",type = FiledType.TEXT )
    private String nsrmc;

    @SearchField(name = "fddbrxm",type = FiledType.TEXT)
    private String fddbrxm;
}
