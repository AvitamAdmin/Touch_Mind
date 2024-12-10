package com.touchmind.core.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document("ShopNavigation")
@Getter
@Setter
@NoArgsConstructor
public class ShopNavigation extends CommonFields {
    private String site;
    private String subsidiary;
    private String variant;
    private String node;
    private String shopCampaign;
    private Map<String, List<NavigationLink>> navigationTree;
    private List<String> activeComponents;
}
