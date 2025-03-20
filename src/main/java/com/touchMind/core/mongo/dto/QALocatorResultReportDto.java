package com.touchMind.core.mongo.dto;


import com.aventstack.extentreports.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class QALocatorResultReportDto extends CommonDto {

    private ObjectId qaTestResultId;
    private String locatorIdentifier;
    private String imgUrl;
    private Status stepStatus;
    private String message;
    private String errorType;
    private String method;


}
