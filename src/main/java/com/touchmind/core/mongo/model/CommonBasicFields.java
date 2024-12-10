package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonBasicFields extends CommonPrimaryFields {
    private String identifier;
    private String shortDescription;
    private Boolean status;
}
