package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CronTestPlanDto extends CommonDto {
    private String testProfile;
    private String categoryId;
    private String environment;
    private String subsidiary;
    private String testPlan;
    private String siteIsoCode;
    //private String subEmails;
    private String cronProfileId;
}
