package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Getter
@Setter
public class CommonPrimaryFields implements Serializable {
    @Id
    @Field("id")
    private ObjectId id;
}
