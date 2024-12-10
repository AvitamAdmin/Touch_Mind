package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.ActionWsDto;

public interface ActionService {
    ActionWsDto handleEdit(ActionWsDto request);

}
