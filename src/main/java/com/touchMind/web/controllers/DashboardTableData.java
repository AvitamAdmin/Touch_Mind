package com.touchMind.web.controllers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DashboardTableData {
    private String subsidiary;
    private String site;
    private String testCase;
    private String status;
    private String passedSkus;
    private String failedSkus;
    private int impact;
}
