package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.RoleWsDto;

public interface RoleService {

    RoleWsDto handelCopy(RoleWsDto request);

    RoleWsDto handleEdit(RoleWsDto request);
}
