package com.touchMind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SchedulerForm extends BaseForm {
    private String cronExpression;
    private List<String> sites;
    private List<String> shortcuts;
    private String subsidiary;
    private String mapping;
    private String emails;
    private String nodePath;
    private String skus;
    private String voucherCode;
    private String jobStatus;
    private Boolean enableHistory;
}
