package com.touchMind.core.mongo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("QaResultReport")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class QaResultReport extends CommonFields {
    @JsonIgnore
    private String sessionId;
    @JsonIgnore
    private String testCaseId;
    @JsonIgnore
    private String sku;
    private int testCaseStatus;
    private String campaign;
    private List<QaLocatorResultReport> qaLocatorResultReports;
    private String errorType;
    private String errorMessage;
}
