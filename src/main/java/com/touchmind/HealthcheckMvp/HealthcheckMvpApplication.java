package com.touchmind.HealthcheckMvp;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication
@OpenAPIDefinition(
        servers = {
                @Server(url = "/", description = "Default Server URL")
        }
)
@ComponentScan(
        {
                "com.touchmind.web.filter",
                "com.touchmind.utils",
                "com.touchmind.web.controllers",
                "com.touchmind.HealthcheckMvp",
                "com.touchmind.storage",
                "com.touchmind.selenium",
                "com.touchmind.core",
                "com.touchmind.excel",
                "com.touchmind.qa",
                "com.touchmind.qa.framework",
                "com.touchmind.selenium.data",
                "com.touchmind.validation",
                "com.touchmind.tookit.service",
                "com.touchmind.mail",
                "com.touchmind.listener",
                //"com.touchmind.core.mongo.repository",
                //"com.touchmind.core.mongo.model",
                "com.touchmind.data",
                //"com.touchmind.data.service.impl",
                "com.touchmind.fileimport.service",
                "com.touchmind.fileimport.strategies",
                "com.touchmind.fileimport.actions",
        }
)
@EnableScheduling
@EnableRetry
@EnableAsync
public class HealthcheckMvpApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(HealthcheckMvpApplication.class, args);
        context.getBean(ApplicationCronJobs.class).scheduleJobs();
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        var source = new ResourceBundleMessageSource();
        source.setBasename("message");
        source.setDefaultEncoding("UTF-8");
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        return sessionLocaleResolver;
    }

    @Bean
    public ScheduledTaskRegistrar scheduledTaskRegistrar() {
        ScheduledTaskRegistrar scheduledTaskRegistrar = new ScheduledTaskRegistrar();
        scheduledTaskRegistrar.setScheduler(threadPoolTaskScheduler());
        return scheduledTaskRegistrar;
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(20);
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.getConfiguration().setCollectionsMergeEnabled(false);
        return mapper;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    JavaMailSenderImpl mailSender() {
        return new JavaMailSenderImpl();
    }

    @Bean
    public Locale locale() {
        return Locale.ENGLISH;
    }

    @Bean
    public FilterRegistrationBean<com.touchmind.web.filter.RequestResponseLoggingFilter> loggingFilter() {
        FilterRegistrationBean<com.touchmind.web.filter.RequestResponseLoggingFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new com.touchmind.web.filter.RequestResponseLoggingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);

        return registrationBean;
    }

    @PostConstruct
    public void init() {
        // Setting Spring Boot SetTimeZone
        TimeZone.setDefault(TimeZone.getTimeZone("CET"));
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder().create();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
