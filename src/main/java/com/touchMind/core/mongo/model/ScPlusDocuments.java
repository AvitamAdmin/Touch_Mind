package com.touchMind.core.mongo.model;

import com.touchMind.core.mongo.model.baseEntity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ScPlusDocuments")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ScPlusDocuments extends BaseEntity {

}
