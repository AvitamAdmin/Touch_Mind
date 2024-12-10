package com.cheil.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class QATestResultDto extends CommonDto {
    private String sessionId;
    private String subsidiary;
    private String user;
    private String testName;
    private String testNameDescription;
    private Integer testPassedCount;
    private Integer testFailedCount;
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
}
