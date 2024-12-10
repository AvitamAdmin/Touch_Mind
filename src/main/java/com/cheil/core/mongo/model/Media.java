package com.cheil.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Media")
@Getter
@Setter
@NoArgsConstructor
public class Media extends CommonFields {
    private String fileName;
}
