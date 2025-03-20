package com.touchMind.form;

import com.touchMind.core.mongo.dto.CronTestPlanDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CronForm extends BaseForm {
    private String identifier;
    private String emailSubject;
    private String ids;

    private String cronExpression;

    private String skus;

    private String jobStatus;
    private String dashboard;
    private String siteUrl;
    private String cronProfileId;
    private List<CronTestPlanDto> cronTestPlanDtoList;
    private String campaign;
}
