package com.touchMind.core.mongo.model.baseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class BaseEntity {
    @Id
    @Field("id")
    private ObjectId id;
    private String identifier;
    private String relationId;
    private String indexId;
    private String sessionId;
    private Map<String, Object> records;
}
