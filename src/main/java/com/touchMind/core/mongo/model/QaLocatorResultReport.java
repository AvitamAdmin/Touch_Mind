package com.touchMind.core.mongo.model;

import com.aventstack.extentreports.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("QaLocatorResultReport")
@Getter
@Setter
@NoArgsConstructor
public class QaLocatorResultReport extends CommonFields {
    private ObjectId qaTestResultId;
    private String locatorIdentifier;
    private String imgUrl;
    private Status stepStatus;
    private String message;
    private String errorType;
    private String method;
    private String component;


    public QaLocatorResultReport getQaLocatorResultReport(ObjectId qaTestResultId, String locatorIdentifier, String shortDescription, String imgUrl, Status stepStatus, String message, String method) {
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport.setQaTestResultId(qaTestResultId);
        qaLocatorResultReport.setLocatorIdentifier(locatorIdentifier);
        qaLocatorResultReport.setShortDescription(shortDescription);
        qaLocatorResultReport.setImgUrl(imgUrl);
        qaLocatorResultReport.setStepStatus(stepStatus);
        qaLocatorResultReport.setMessage(message);
        qaLocatorResultReport.setMethod(method);
        return qaLocatorResultReport;
    }
}
