package com.cheil.form;

import com.cheil.core.mongo.model.EnvironmentConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EnvironmentForm extends BaseForm {
    private String modifier;
    private String identifier;
    private String shortDescription;
    private List<String> subsidiaries;
    private List<EnvironmentConfig> configs;
}

