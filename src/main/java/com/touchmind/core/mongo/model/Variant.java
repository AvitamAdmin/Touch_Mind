package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("Variant")
@Getter
@Setter
@NoArgsConstructor
public class Variant extends CommonFields {
    private String externalProductUrl;
    private Model model;
    private Category category;
    private String pageType;
    private List<String> subsidiaries;
}
