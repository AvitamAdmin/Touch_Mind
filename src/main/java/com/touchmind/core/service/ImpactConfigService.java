package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.ImpactConfigWsDto;
import com.touchmind.core.mongo.model.ImpactConfig;
import com.touchmind.form.ImpactConfigForm;

public interface ImpactConfigService {
    ImpactConfig saveConfig(ImpactConfigForm impactConfigForm);

    ImpactConfigWsDto handleEdit(ImpactConfigWsDto request);
}
