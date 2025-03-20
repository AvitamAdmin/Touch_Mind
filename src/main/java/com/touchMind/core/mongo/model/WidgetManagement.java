package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("WidgetDisplayType")
@Getter
@Setter
@NoArgsConstructor
public class WidgetManagement extends CommonFields {
    private String widgetType;
    private String columns;
    private String dashboard;
    private String dashboardPosition;
    private String report;
}
