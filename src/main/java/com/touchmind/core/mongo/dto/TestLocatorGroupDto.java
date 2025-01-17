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
public class TestLocatorGroupDto extends CommonDto {
    private List<LocatorPriorityDto> testLocators;
  //  private String subsidiary;
   // private List<ConditionGroupDto> conditionGroupList;
    private Boolean checkEppSso;
    private Boolean published;
    private Boolean takeAScreenshot;
}
