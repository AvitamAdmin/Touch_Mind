package com.touchmind.core.mongo.model;

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
public class QaLocatorResultReport extends CommonFields  {
    private ObjectId qaTestResultId;
    private String locatorIdentifier;
    private String imgUrl;
    private Status stepStatus;
    private String message;
    public QaLocatorResultReport getQaLocatorResultReport(ObjectId qaTestResultId,String locatorIdentifier,String shortDescription,String imgUrl,Status stepStatus,String message) {
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport.setQaTestResultId(qaTestResultId);
        qaLocatorResultReport.setLocatorIdentifier(locatorIdentifier);
        qaLocatorResultReport.setShortDescription(shortDescription);
        qaLocatorResultReport.setImgUrl(imgUrl);
        qaLocatorResultReport.setStepStatus(stepStatus);
        qaLocatorResultReport.setMessage(message);
        return qaLocatorResultReport;
    }
}
