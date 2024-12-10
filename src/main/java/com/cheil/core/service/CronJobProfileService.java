package com.cheil.core.service;

import com.cheil.core.mongo.dto.CronJobProfileWsDto;

public interface CronJobProfileService {
    CronJobProfileWsDto handleEdit(CronJobProfileWsDto request);
}
