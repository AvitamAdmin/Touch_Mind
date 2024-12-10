package com.touchmind.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = {"com.touchmind.core.mongotemplate"},
        mongoTemplateRef = DashboardDBConfig.MONGO_TEMPLATE
)
public class DashboardDBConfig {
    protected static final String MONGO_TEMPLATE = "dashboardMongoTemplate";
}


