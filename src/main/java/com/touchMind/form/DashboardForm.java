package com.touchMind.form;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DashboardForm extends BaseForm {

    private String identifier;

    private String node;
    private String themeColor;
    private String template;
    private String chart;
    private String subsidiary;
    private Long dashboardProfile;
}
