package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("TestDataSubtype")
@Getter
@Setter
@NoArgsConstructor
public class TestDataSubtype extends CommonFields {
    private String testDataType;
   // private List<String> subsidiaries;
}
