package com.touchmind.core.mongo.dto;

import com.touchmind.core.mongo.model.LocatorPriority;
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
    private List<LocatorPriority> testLocators;
    private Boolean published;
    private Boolean takeAScreenshot;
}
