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
public class TestPlanDto extends CommonDto {
    private List<String> testLocatorGroups;
    private String subsidiary;
}
