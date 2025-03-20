package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
public class LocatorGroupFailedResultDto extends CommonDto {
    private String sessionId;
    private ObjectId groupId;
}
