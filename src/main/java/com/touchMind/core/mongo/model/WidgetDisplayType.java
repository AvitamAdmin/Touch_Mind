package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("WidgetDisplayType")
@Getter
@Setter
@NoArgsConstructor
public class WidgetDisplayType extends CommonFields {
    private String height;
    private String dimensions;
}
