package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("System")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class System extends CommonFields {
    private String systemLink;
    private String systemPath;
    private List<String> subsidiaries;
    private List<String> catalogs;
    private List<String> modules;
}
