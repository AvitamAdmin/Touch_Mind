package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DataSourceWsDto extends CommonWsDto {
    private List<DataSourceDto> dataSources;
    private int existingParamsCount;
    private List<String> inputFormats;
    private Set<String> fileFormats;
    private Set<String> params;
}
