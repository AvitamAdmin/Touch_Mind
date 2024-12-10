package com.cheil.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("LocatorsGroupPriority")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class LocatorsGroupPriority extends CommonFields {
    private List<TestLocator> testLocators;
}
