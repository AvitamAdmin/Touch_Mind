package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("operation_check")
@Getter
@Setter
@NoArgsConstructor
public class OperationCheck extends CommonFields {
    private String shortcutName;
    private String shortcutValue;
}

