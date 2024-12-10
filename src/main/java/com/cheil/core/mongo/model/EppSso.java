package com.cheil.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("EppSso")
@Getter
@Setter
@NoArgsConstructor
public class EppSso extends CommonFields {
    private String ssoLink;
    private String affiliateId;
    private String hash;
    private String timestamp;
    private String disabledLink;
}
