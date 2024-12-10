package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("CronHistory")
@Getter
@Setter
public class CronHistory extends CommonFields {
    private String subsidiary;
    private String jobTime;
    private Integer processedSkus;
    private String scheduler;
    private String email;
    private String cronStatus;
    private String errorMsg;
    private String sessionId;
}
