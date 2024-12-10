package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Subsidiary")
@Getter
@Setter
@NoArgsConstructor
public class Subsidiary extends CommonFields {
    private String cluster;
    private String isoCode;
    private String localeLanguage;
}
