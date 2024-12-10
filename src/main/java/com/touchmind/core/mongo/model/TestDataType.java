package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("TestDataType")
@Getter
@Setter
@NoArgsConstructor
public class TestDataType extends CommonFields {
    private List<String> subsidiaries;
}
