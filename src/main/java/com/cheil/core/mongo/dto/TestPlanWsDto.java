package com.cheil.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TestPlanWsDto extends CommonWsDto {
    private List<TestPlanDto> testPlans;
    private List<CronJobProfileDto> cronJobProfiles;
    private Boolean enableBtn;
    private String subsidiary;
    private String fileName;
    private String emailSubject;
    private String siteIsoCode;
    private String testProfile;
    private String environment;
    private String testPlan;
    private String skus;
}
