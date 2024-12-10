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
public class DataRelationDto extends CommonDto {
    private List<DataRelationParamsDto> dataRelationParams;
    private Boolean enableGenerator;
}
