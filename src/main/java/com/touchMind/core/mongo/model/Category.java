package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Document("Category")
@Getter
@Setter
public class Category extends CommonFields {
    private String parentId;
    private List<String> childIds;
    private Set<String> subsidiaries;
}
