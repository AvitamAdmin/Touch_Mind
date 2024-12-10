package com.cheil.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConditionGroup {
    private String toolkitId;
    private String paramName;
    private String condition;
    private String paramValue;
    private Boolean isOrChain;
}
