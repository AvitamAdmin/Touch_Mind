package com.cheil.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("SchedulerJobs")
@Getter
@Setter
@NoArgsConstructor
public class SchedulerJob extends CommonFields {
    private String cronId;
    private String cronExpression;
    private String sites;
    private String shortcuts;
    private String subsidiary;
    private String mapping;
    private String emails;
    private String nodePath;
    private String skus;
    private String voucherCode;
    private String jobStatus;
    private Boolean enableHistory;
    private String interfaceName;
}
