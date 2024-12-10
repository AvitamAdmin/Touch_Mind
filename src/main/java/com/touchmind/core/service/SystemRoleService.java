package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.SystemRoleWsDto;

public interface SystemRoleService {
    SystemRoleWsDto handleEdit(SystemRoleWsDto request);
}
