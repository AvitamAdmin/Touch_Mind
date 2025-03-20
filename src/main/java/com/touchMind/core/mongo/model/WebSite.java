package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Document("WebSite")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class WebSite extends CommonFields {
    private String reportPassDeleteDays;
    private String reportFailedDeleteDays;
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date qaResultReportStartDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date qaResultReportEndDate;
}
