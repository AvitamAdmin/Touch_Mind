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
public class SourceTargetParamMappingWsDto extends CommonWsDto {
    private List<SourceTargetMappingDto> sourceTargetMappings;
}