package com.touchMind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class OperationForm extends BaseForm {
    private String skus;
    private String checkType;
    private String[] sites;
    private String[] checkOptions;
}
