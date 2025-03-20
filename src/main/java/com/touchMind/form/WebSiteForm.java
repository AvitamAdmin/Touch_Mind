package com.touchMind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class WebSiteForm extends BaseForm implements Serializable {
    private String identifier;
    private String shortDescription;
    private String reportPassDeleteDays;
    private String reportFailedDeleteDays;
    private String qaResultReportStartDate;
    private String qaResultReportEndDate;
}
