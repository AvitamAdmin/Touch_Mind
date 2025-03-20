package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TariffDto extends CommonDto {
    private String sessionId;
    private String planId;
    private String deviceId;
    private String tariffName;
    private Double otp;
    private Date updatedDate;
    private boolean isActive;
}
