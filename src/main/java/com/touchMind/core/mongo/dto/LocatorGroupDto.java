package com.touchMind.core.mongo.dto;

import com.touchMind.core.mongo.model.LocatorPriority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LocatorGroupDto extends CommonDto {
    private List<LocatorPriority> testLocators;
    private boolean applyGlobally;
    private boolean clearHardFailed;
}
