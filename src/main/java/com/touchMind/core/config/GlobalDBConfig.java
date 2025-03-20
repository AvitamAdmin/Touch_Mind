package com.touchMind.core.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = {"com.touchMind.core.mongo"},
        mongoTemplateRef = GlobalDBConfig.MONGO_TEMPLATE
)
public class GlobalDBConfig {
    protected static final String MONGO_TEMPLATE = "mongoTemplate";
}


