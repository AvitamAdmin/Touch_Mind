package com.cheil.qa.template.data;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class QaTestResultData {
    private String testName;
    private String reportFilePath;
    private Object locatorGroupIdentifier;
    private int testPassedCount;
    private int testFailedCount;
    private Date startTime;
    private long timeTaken;
    private Date endTime;
    private int resultStatus;
}
