package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.CronJobProfileWsDto;

public interface CronJobProfileService {
    CronJobProfileWsDto handleEdit(CronJobProfileWsDto request);
}
