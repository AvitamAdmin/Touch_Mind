package com.cheil.core.mongo.model;

import com.cheil.core.mongo.model.baseEntity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("AddonDocuments")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AddonDocuments extends BaseEntity {

}