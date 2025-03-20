package com.touchMind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SelectorForm extends BaseForm {
    private String identifier;
    private String shortDescription;
    private String testPlan;
    private List<SelectorConfigForm> selectorConfigs;
}
