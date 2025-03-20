package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.model.User;
import com.touchMind.core.mongo.repository.UserRepository;
import com.touchMind.core.service.CoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class CoreServiceImpl implements CoreService {

    private final Logger logger = LoggerFactory.getLogger(CoreServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        return userRepository.findByUsername(principal.getUsername());
    }
}
