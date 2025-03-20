package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ErrorType")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ErrorType extends CommonFields {
    private String message;
}
