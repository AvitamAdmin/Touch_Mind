package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import com.touchmind.core.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class RepositoryServiceImpl implements RepositoryService {

    private static final Logger LOG = LoggerFactory.getLogger(RepositoryServiceImpl.class);

    @Autowired
    private ApplicationContext applicationContext;


    @Override
    public GenericImportRepository getRepositoryForName(String entityName) {
        try {
            return (GenericImportRepository) applicationContext.getBean(entityName + "Repository");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }
}
