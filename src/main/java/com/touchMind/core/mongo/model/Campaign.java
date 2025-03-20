package com.touchMind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("Campaign")
@Getter
@Setter
@NoArgsConstructor
public class Campaign extends CommonFields {
    private List<String> domPaths;
}

