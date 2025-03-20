package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Systemroles")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SystemRole extends CommonFields {
    private String description;
    private System system;
}
