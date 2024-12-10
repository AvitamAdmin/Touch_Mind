package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TestLocatorGroupWsDto extends CommonWsDto {
    private List<TestLocatorGroupDto> testLocatorGroups;
    private int existingParamsCount;
    private int existingConditionCount;
    private List<String> conditionOperators;
}
