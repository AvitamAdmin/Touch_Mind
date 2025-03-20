package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.RoleWsDto;

public interface RoleService {

    RoleWsDto handelCopy(RoleWsDto request);

    RoleWsDto handleEdit(RoleWsDto request);
}
