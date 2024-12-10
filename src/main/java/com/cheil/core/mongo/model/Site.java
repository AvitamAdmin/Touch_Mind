package com.cheil.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Site")
@Getter
@Setter
@NoArgsConstructor
public class Site extends CommonFields {
    private String affiliateId;
    private String affiliateName;
    private String siteChannel;
    private String secretKey;
    private String subsidiary;
}
