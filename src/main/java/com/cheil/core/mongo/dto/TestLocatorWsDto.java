package com.cheil.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TestLocatorWsDto extends CommonWsDto {
    private List<TestLocatorDto> testLocators;
    private List<String> SELECTOR_STRATEGIES;
    private List<String> methods;
}
