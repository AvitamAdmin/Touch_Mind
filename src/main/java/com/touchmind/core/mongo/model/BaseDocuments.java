package com.touchmind.core.mongo.model;

import com.touchmind.core.mongo.model.baseEntity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("BaseDocuments")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BaseDocuments extends BaseEntity {

}
