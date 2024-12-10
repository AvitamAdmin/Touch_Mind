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
public class SchedulerJobWsDto extends CommonWsDto {
    private List<SchedulerJobDto> schedulerJobs;
    private List<SourceTargetMappingDto> sourceTargetMappingList;
}
