package com.cheil.core.mongo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("CronJobProfile")
@Getter
@Setter
public class CronJobProfile extends CommonFields {
    private String recipients;
}
