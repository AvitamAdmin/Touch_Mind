package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SubsidiaryDto extends CommonDto {
    private String cluster;
    private String isoCode;
    private String localeLanguage;
}
