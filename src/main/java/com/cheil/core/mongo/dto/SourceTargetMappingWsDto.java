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
public class SourceTargetMappingWsDto extends CommonWsDto {
    private List<SourceTargetMappingDto> sourceTargetMappings;
    private int existingParamsCount;
    private List<String> params;
}
