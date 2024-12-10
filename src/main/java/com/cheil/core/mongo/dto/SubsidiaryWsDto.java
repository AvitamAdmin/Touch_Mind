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
public class SubsidiaryWsDto extends CommonWsDto {
    private List<SubsidiaryDto> subsidiaries;
    private List<CountryDto> countries;
}
