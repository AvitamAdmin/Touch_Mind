package com.cheil.core.mongo.model;

import com.cheil.core.mongo.model.baseEntity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("EupDocuments")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class EupDocuments extends BaseEntity {

}
