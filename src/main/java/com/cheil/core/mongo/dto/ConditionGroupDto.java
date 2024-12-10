package com.cheil.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ConditionGroupDto extends CommonDto {
    private String toolkitId;
    private String paramName;
    private String condition;
    private String paramValue;
    private Boolean isOrChain;
}
