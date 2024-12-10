package com.touchmind.core.mongo.dto;

import com.touchmind.core.mongo.model.TestLocator;
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
