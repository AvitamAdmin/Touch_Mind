package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.SystemRoleWsDto;

public interface SystemRoleService {
    SystemRoleWsDto handleEdit(SystemRoleWsDto request);
}
