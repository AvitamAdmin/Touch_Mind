package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Reports")
@Getter
@Setter
@NoArgsConstructor
public class Reports extends CommonFields {
    private String sessionId;
    private String subsidiary;
    private String testName;
    private String site;
    private String campaign;
    private String sku;
    private String method;
    private String message;
    private long occurrenceCount;
    private int priority;
    private String errorType;
}
