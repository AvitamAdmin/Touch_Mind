package com.touchmind.core.mongo.model;

import com.touchmind.core.mongo.dto.SearchQueryDto;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document("SavedQuery")
@Getter
@Setter
public class SavedQuery {
    @Id
    @Field("id")
    private ObjectId id;
    private String operator;
    private String recordId;
    private String identifier;
    private String shortDescription;
    private List<SearchQueryDto> queries;
    private String sourceItem;
    private String user;
}
