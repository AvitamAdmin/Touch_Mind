package com.touchmind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SelectorConfigForm extends BaseForm {
    private String identifier;
    private String className;
    private String methodName;
    private String strategy;
    private String uiSelector;
}
