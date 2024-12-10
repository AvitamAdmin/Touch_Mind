package com.cheil.core.mongo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ParamInput")
@Getter
@Setter
public class ParamInput extends CommonFields {
    private String paramKey;
    private String paramValue;
}
