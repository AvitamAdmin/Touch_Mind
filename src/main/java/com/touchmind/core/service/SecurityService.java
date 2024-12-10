package com.touchmind.core.service;

import jakarta.servlet.http.HttpServletRequest;

public interface SecurityService {
    String findLoggedInUsername();

    void autoLogin(String username, String password, HttpServletRequest request);
}

