package com.cheil.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document("Model")
@Getter
@Setter
@NoArgsConstructor
public class Model extends CommonFields {
    private Set<String> categories;
    private Set<String> subsidiaries;
    private Set<String> variants;
}
