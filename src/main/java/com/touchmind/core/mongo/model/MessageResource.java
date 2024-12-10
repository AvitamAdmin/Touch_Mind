package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("MessageResource")
@Getter
@Setter
@NoArgsConstructor
public class MessageResource extends CommonFields {
    private String testPlanId;
    private String description;
    private Integer percentFailure;
    private String recipients;
    private String type;
}
