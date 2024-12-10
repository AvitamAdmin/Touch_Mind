package com.cheil.core.mongo.model;

import com.cheil.core.mongo.dto.CronTestPlanDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("CronJobs")
@Getter
@Setter
@NoArgsConstructor
public class CronJob extends CommonFields {
    private String emailSubject;
    private String cronExpression;
    private String skus;
    private String jobStatus;
    private Boolean enableHistory = false;
    private String dashboard;
    private String siteUrl;
    private String cronProfileId;
    private List<String> envProfiles;
    private List<CronTestPlanDto> cronTestPlanDtoList;
    private String campaign;
}
