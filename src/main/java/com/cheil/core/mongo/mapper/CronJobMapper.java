package com.cheil.core.mongo.mapper;

import com.cheil.core.mongo.dto.CronJobDto;
import com.cheil.core.mongo.model.CronJob;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CronJobMapper {
    CronJob toEntity(CronJobDto cronJobDto);

    CronJobDto toDto(CronJob cronJob);

    List<CronJobDto> toDtoList(List<CronJob> cronJobs);
}
