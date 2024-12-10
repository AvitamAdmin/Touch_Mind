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
public class QATestResultWsDto extends CommonWsDto {
    List<QATestResultDto> qaTestResults;
    private String dashboard;
}
