package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DataSourceInputDto extends CommonDto {
    private String dataSourceId;

    private String fieldName;

    private String inputFormat;

    private String fieldValue;

    private String fileName;

    private String comma;

    private String fixed;

    private String optional;

    private String importBox;
}
