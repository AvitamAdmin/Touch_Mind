package com.touchmind.core.mongotemplate;

import com.touchmind.core.mongo.model.CommonFields;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;
import java.util.Set;

@Document("QATestResult")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class QATestResult extends CommonFields {
    private String sessionId;
    private String user;
    private String testName;
    private String testNameDescription;
    private int testPassedCount;
    private int testFailedCount;
    private Integer resultStatus;
    private String orderNumber;
    private String reportFilePath;
    private Date startTime;
    private Date endTime;
    private Long timeTaken;
    private String sku;
    private Map<String, String> failedSkusError;
    private Object locatorGroupIdentifier;
    private String paymentType;
    private String deliveryType;
    private String resultMessage;
    private String dashboard;
    private Map<String, Set<String>> errorMap;
    private String site;
    //TODO set campaign here
    private String campaign;
    private String errorType;
    private String errorMessage;
}
