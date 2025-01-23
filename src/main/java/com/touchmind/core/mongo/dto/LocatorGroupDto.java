package com.touchmind.core.mongo.dto;

import com.touchmind.core.mongo.model.LocatorPriority;
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
    private List<LocatorPriority> testLocators;
    private String subsidiary;
    private List<LocatorGroupDto> locatorGroupDtoList;
    private boolean checkEppSso;
    private String errorMsg;
    private Long priority;
    private Boolean published;
    private Boolean takeAScreenshot;
}
