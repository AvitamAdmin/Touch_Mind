package com.cheil.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("Environment")
@Getter
@Setter
@NoArgsConstructor
public class Environment extends CommonFields {
    private String modifier;
    private List<String> subsidiaries;
    private List<EnvironmentConfig> configs;
}

