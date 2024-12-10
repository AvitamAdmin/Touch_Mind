package com.cheil.core.service;

import com.cheil.core.mongo.dto.ImpactConfigWsDto;
import com.cheil.core.mongo.model.ImpactConfig;
import com.cheil.form.ImpactConfigForm;

public interface ImpactConfigService {
    ImpactConfig saveConfig(ImpactConfigForm impactConfigForm);

    ImpactConfigWsDto handleEdit(ImpactConfigWsDto request);
}
