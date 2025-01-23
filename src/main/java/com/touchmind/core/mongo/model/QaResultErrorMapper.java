package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("QaResultErrorMapper")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class QaResultErrorMapper extends CommonFields {
    private String locatorRegEx;
    private String descriptionRegEx;
    private String messageRegEx;
    private Long errorType;
    private String errorMessage;
}
