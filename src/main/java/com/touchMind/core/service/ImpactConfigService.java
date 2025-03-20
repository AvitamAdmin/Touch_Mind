package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.ImpactConfigWsDto;
import com.touchMind.core.mongo.model.ImpactConfig;
import com.touchMind.form.ImpactConfigForm;

public interface ImpactConfigService {
    ImpactConfig saveConfig(ImpactConfigForm impactConfigForm);

    ImpactConfigWsDto handleEdit(ImpactConfigWsDto request);
}
