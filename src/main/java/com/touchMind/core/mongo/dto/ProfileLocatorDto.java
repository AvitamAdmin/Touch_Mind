package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProfileLocatorDto extends CommonDto {
    private String locatorId;
    private String inputValue;
    private String description;
    private String testDataType;
}
