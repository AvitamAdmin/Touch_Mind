package com.cheil.core.mongo.model;

import com.cheil.core.mongo.model.baseEntity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("BenefitsDocuments")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BenefitsDocuments extends BaseEntity {

}
