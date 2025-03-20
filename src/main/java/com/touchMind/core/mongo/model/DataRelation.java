package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("DataRelation")
@Getter
@Setter
@NoArgsConstructor
public class DataRelation extends CommonFields {
    @DBRef(lazy = true)
    private List<DataRelationParams> dataRelationParams;
    private Boolean enableGenerator;
}
