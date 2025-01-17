package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DashboardDto extends CommonDto {
    private String node;
    private String themeColor;
    private String template;
    private String chart;
  //  private String subsidiary;
    private String dashboardProfile;
}
