package com.touchmind.core;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.filters.RegexPatternFileListFilter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.File;

@Configuration
@EnableIntegration
public class ApplicationConfig implements HotFolderConstants {

    public static final String FILE_PATTERN = ".*\\.*";
    @Autowired
    private Environment env;

    @Bean
    public MessageChannel fileChannel() {
        return new DirectChannel();
    }

    @Bean
    @InboundChannelAdapter(value = "fileChannel", poller = @Poller(fixedDelay = "1000"))
    public MessageSource<File> fileReadingMessageSource() {
        File directory = new File(StringUtils.isNotEmpty(env.getProperty("hot.folder.location")) ? env.getProperty("hot.folder.location") : DEFAULT_HOT_FOLDER_LOCATION);
        RegexPatternFileListFilter filter = new RegexPatternFileListFilter(FILE_PATTERN);
        return new FilePoller(directory, filter);
    }

    @Bean
    @ServiceActivator(inputChannel = "fileChannel")
    public MessageHandler fileWritingMessageHandler() {
        return new FileHandler();
    }


}