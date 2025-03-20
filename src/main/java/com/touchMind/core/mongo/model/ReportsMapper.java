package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ReportsMapper")
@Getter
@Setter
@NoArgsConstructor
public class ReportsMapper extends CommonFields {
    private String subsidiary;
    private String site;
    private String campaign;
    private String pattern;
    private int priority;
}
