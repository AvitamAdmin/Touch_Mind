package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportsMapperDto extends CommonDto {
    private String subsidiary;
    private String site;
    private String campaign;
    private String pattern;
    private int priority;
}
