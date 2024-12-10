package com.cheil.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DataRelationParamsDto extends CommonDto {
    private String sourceKeyOne;
    private Long dataSource;
}