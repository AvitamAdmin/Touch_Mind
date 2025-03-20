package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("SourceTargetParamMapping")
@Getter
@Setter
@NoArgsConstructor
public class SourceTargetParamMapping extends CommonFields {
    private String header;
    private String param;
    private String dataSource;
    private Boolean isPivot;
}
