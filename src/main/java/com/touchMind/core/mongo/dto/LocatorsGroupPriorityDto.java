package com.touchMind.core.mongo.dto;

import com.touchMind.core.mongo.model.TestLocator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LocatorsGroupPriorityDto extends CommonDto {
    private List<TestLocator> testLocators;
}
