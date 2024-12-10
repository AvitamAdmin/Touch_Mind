package com.touchmind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CascadeTestPlanForm extends BaseForm {
    private String identifier;
    private String shortDescription;
    private List<String> testProfileIds;
}
