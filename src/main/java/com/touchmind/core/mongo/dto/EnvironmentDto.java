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
public class EnvironmentDto extends CommonDto {
    private String modifier;
    private List<String> subsidiaries;
    private List<EnvironmentConfigDto> configs;
}
