package com.cheil.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LocatorGroupDto extends CommonDto {
    private String shortDescription;
    private String identifier;
    private List<LocatorPriorityDto> testLocators;
    private String subsidiary;
    private List<LocatorGroupDto> locatorGroupDtoList;
    private List<ConditionGroupDto> conditionGroupList;
    private boolean checkEppSso;
    private String errorMsg;
    private Long priority;
    private Boolean published;
    private Boolean takeAScreenshot;
}
