package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Locale;


@Document("Country")
@Getter
@Setter
@NoArgsConstructor
public class Country extends CommonFields {
    private Locale locale;
}
