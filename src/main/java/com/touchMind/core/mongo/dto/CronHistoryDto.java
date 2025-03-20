package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CronHistoryDto extends CommonDto {
    private String subsidiary;
    private String jobTime;
    private Integer processedSkus;
    private String scheduler;
    private String email;
    private String cronStatus;
    private String errorMsg;
    private String sessionId;
}
