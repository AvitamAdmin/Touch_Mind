package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Dashboard")
@Getter
@Setter
@NoArgsConstructor
public class Dashboard extends CommonFields {
    private String node;
    private String themeColor;
    private String template;
    private String chart;
   // private String subsidiary;
    private String dashboardProfile;
}
