package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SchedulerJobDto extends CommonDto {
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
