package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Module")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Module extends CommonFields {
    private String systemLink;
    private String systemPath;
    private System system;
}