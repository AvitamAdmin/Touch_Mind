package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DashboardReportDto extends CommonDto {
    private String type;
    private String dashboard;
    private String widgets;
    private String originalTable;
    private String params;
    private String filters;
    private String name;
}
