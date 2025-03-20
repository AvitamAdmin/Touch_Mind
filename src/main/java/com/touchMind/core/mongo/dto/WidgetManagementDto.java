package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class WidgetManagementDto extends CommonDto {
    private String widgetType;
    private String columns;
    private String dashboard;
    private String dashboardPosition;
    private String report;
}
