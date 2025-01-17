package com.touchmind.form;

import com.touchmind.core.mongo.model.baseEntity.QABaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestProfileObject extends QABaseEntity {
   // private String subsidiary;
    private String identifier;
    private String shortDescription;
}
