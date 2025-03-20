package com.touchMind.qa.service.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class CronDefinition {
    private String id;
    private String cronExpression;
    private String jobTime;
    private String status;
    private String email;
    private String title;
    private Map<String, String> data;
    private String filename;
    private String reportUrl;

}
