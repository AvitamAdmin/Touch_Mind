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
public class DataSourceDto extends CommonDto {
    private String format;
    private String sourceAddress;
    private String skuUrl;
    private List<String> srcInputParams;
    private SourceTargetParamMappingDto sourceTargetParamMapping;
    private String targetProcess;
    private List<DataSourceInputDto> dataSourceInputs;
    private String separatorSymbol;
}
