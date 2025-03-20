package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("DataRelationParams")
@Getter
@Setter
@NoArgsConstructor
public class DataRelationParams extends CommonFields {
    private String sourceKeyOne;
    private String dataSource;
    @DBRef(lazy = true)
    private DataRelation dataRelation;
}
