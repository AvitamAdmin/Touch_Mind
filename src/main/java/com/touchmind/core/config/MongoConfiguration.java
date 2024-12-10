package com.touchmind.core.config;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MongoConfiguration {

    @Autowired
    Environment environment;


    @Primary
    @Bean(name = "globalDBProperties")
    @ConfigurationProperties(prefix = "spring.data.mongodb")
    public MongoProperties getGlobalMongoProps() throws Exception {
        return new MongoProperties();
    }

    @Bean(name = "dashboardDBProperties")
    @ConfigurationProperties(prefix = "spring.data.mongodb.dashboard")
    public MongoProperties getMongoDashboardProps() throws Exception {
        return new MongoProperties();
    }

    @Primary
    @Bean(name = "mongoTemplate")
    public MongoTemplate gloablMongoTemplate() throws Exception {
        return new MongoTemplate(globalMongoDatabaseFactory(getGlobalMongoProps()));
    }

    @Bean(name = "dashboardMongoTemplate")
    public MongoTemplate dashboardMongoTemplate() throws Exception {
        return new MongoTemplate(dashboardMongoDatabaseFactory(getMongoDashboardProps()));
    }

    @Primary
    @Bean
    public MongoDatabaseFactory globalMongoDatabaseFactory(MongoProperties mongo) throws Exception {
        return new SimpleMongoClientDatabaseFactory(globalMongoClient(mongo), mongo.getDatabase()
        );
    }

    @Bean
    public MongoDatabaseFactory dashboardMongoDatabaseFactory(MongoProperties mongo) throws Exception {
        return new SimpleMongoClientDatabaseFactory(dashboardMongoClient(mongo), mongo.getDatabase()
        );
    }

    @Bean
    public MongoClient dashboardMongoClient(@Qualifier("dashboardDBProperties") MongoProperties mongoProperties) {
        return createMongoClient(mongoProperties);
    }

    private MongoClient createMongoClient(MongoProperties mongoProperties) {
        if (StringUtils.isNotEmpty(mongoProperties.getUri())) {
            String connectionString = mongoProperties.getUri();
            String truststore = environment.getProperty("ssl.trust-store-location");
            String truststorePassword = environment.getProperty("ssl.trust-store-password");

            if (StringUtils.isNotEmpty(truststore) && StringUtils.isNotEmpty(truststorePassword)) {
                System.setProperty("javax.net.ssl.trustStore", truststore);
                System.setProperty("javax.net.ssl.trustStorePassword", truststorePassword);
            }
            return MongoClients.create(connectionString);
        }
        return MongoClients.create("mongodb://localhost:27017");
    }

    @Primary
    @Bean
    public MongoClient globalMongoClient(@Qualifier("globalDBProperties") MongoProperties mongoProperties) {
        return createMongoClient(mongoProperties);
    }
}


