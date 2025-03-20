package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document("LocatorGroupFailedResult")
public class LocatorGroupFailedResult extends CommonFields {
    private String sessionId;
    private String groupId;
}

