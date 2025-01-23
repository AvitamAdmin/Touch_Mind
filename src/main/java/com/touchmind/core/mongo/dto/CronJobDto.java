package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CronJobDto extends CommonDto {
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
    private String emails;
}
