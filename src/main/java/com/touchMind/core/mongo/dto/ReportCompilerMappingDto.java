package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReportCompilerMappingDto {
    private String header;
    private String param;
    private String dataSource;
    private String sourceTargetMapping;
    private Boolean isPivot;
}
