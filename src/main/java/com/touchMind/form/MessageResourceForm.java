package com.touchMind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageResourceForm extends BaseForm {
    private Long testPlanId;
    private String identifier;
    private String description;
    private Integer percentFailure;
    private String recipients;
    private String type;
}

