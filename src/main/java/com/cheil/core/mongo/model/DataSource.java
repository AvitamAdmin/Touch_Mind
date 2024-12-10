package com.cheil.core.mongo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Document("DataSource")
@Getter
@Setter
public class DataSource extends CommonFields {
    private String format;
    private String sourceAddress;
    private String skuUrl;
    private List<String> srcInputParams;
    @DBRef(lazy = true)
    private SourceTargetParamMapping sourceTargetParamMapping;
    private String targetProcess;
    @DBRef(lazy = true)
    private List<DataSourceInput> dataSourceInputs;
    private String separatorSymbol;
}
