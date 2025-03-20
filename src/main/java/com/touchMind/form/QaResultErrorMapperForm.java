package com.touchMind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class QaResultErrorMapperForm extends BaseForm implements Serializable {
    private String identifier;
    private String locatorRegEx;
    private String descriptionRegEx;
    private String messageRegEx;
    private String errorType;
    private String errorMessage;
}
