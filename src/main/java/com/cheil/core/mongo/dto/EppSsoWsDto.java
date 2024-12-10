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
public class EppSsoWsDto extends CommonWsDto {
    private List<EppSsoDto> eppSsos;
    private List<String> sites;
    private String timeZone;
    private String ssoDate;
    private String environment;
    private String subsidiary;
}
