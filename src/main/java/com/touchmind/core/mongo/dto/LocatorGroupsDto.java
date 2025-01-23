package com.touchmind.core.mongo.dto;

import com.touchmind.core.mongo.model.LocatorPriority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LocatorGroupsDto extends CommonDto {
    private List<LocatorPriority> testLocators;
    private boolean applyGlobally;
}
