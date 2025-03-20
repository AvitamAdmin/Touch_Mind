package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("DataSourceInput")
@Getter
@Setter
public class DataSourceInput extends CommonFields {

    private String dataSourceId;

    private String fieldName;

    private String inputFormat;

    private String fieldValue;

    private String fileName;

    private String comma;

    private String fixed;

    private String optional;

    private String importBox;
}
