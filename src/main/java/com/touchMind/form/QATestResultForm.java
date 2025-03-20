package com.touchMind.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class QATestResultForm {
    private String id;
    private String identifier;
    private String errorType;
    private String errorMessage;
    private int resultStatus;
}
