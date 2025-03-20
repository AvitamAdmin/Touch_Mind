package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("DashboardReport")
@Getter
@Setter
@NoArgsConstructor
public class DashboardReport extends CommonFields {
    private String type;
    private String dashboard;
    private String widgets;
    private String originalTable;
    private String params;
    private String filters;
    private String name;
}
