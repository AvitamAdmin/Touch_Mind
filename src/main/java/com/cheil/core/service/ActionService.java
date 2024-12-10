package com.cheil.core.service;

import com.cheil.core.mongo.dto.ActionWsDto;

public interface ActionService {
    ActionWsDto handleEdit(ActionWsDto request);

}
