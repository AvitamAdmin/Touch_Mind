package com.touchmind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ParamInputForm extends BaseForm {
    private String paramKey;
    private String paramValue;
}

