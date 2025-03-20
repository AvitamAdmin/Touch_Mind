package com.touchMind.core.mongo.dto;

import com.touchMind.core.mongo.model.LocatorPriority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TestLocatorGroupDto extends CommonDto {
    private List<LocatorPriority> testLocators;
    private String subsidiary;
    private List<ConditionGroupDto> conditionGroupList = new ArrayList<>();
    private Boolean checkEppSso;
    private Boolean published;
    private Boolean takeAScreenshot;
    private String errorMsg;
}
