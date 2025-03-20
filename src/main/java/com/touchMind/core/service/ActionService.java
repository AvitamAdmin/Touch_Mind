package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.ActionWsDto;

public interface ActionService {
    ActionWsDto handleEdit(ActionWsDto request);

}
