package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document("LocatorGroup")
public class LocatorGroup extends CommonFields {
    private List<LocatorPriority> testLocators;
    private boolean applyGlobally;
}
