package com.cheil.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor


public class VariantWsDto extends CommonWsDto {
    private List<VariantDto> variants;
    private String fileName;
}
