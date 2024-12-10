package com.cheil.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("TestPlan")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TestPlan extends CommonFields {
    private List<String> testLocatorGroups;
    private String subsidiary;
}
