package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SourceTargetParamMappingDto {
    private String header;
    private String param;
    private String dataSource;
    private Boolean isPivot;
}
