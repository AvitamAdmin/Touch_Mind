package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("TestLocatorGroup")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TestLocatorGroup extends CommonFields {
    private List<LocatorPriority> testLocators;
    private Boolean published;
    private Boolean takeAScreenshot;
}
