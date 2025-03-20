package com.touchMind.core.mongo.dto;

import com.touchMind.form.LocatorSelectorDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TestLocatorDto extends CommonDto {
    List<TestLocatorGroupDto> testLocatorGroups;
    private String methodName;
    private SortedMap<String, LocatorSelectorDto> uiLocatorSelector;
    private Map<String, String> inputData;
    private Set<String> labels;
    private String errorMsg;
    private Boolean encrypted;
    private String expression;
    private List<String> subLocators;
}
