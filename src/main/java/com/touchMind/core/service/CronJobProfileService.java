package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.CronJobProfileWsDto;

public interface CronJobProfileService {
    CronJobProfileWsDto handleEdit(CronJobProfileWsDto request);
}
