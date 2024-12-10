package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ShopNavigationDto extends CommonDto {
    private String identifier;
    private String site;
    private String subsidiary;
    private String variant;
    private String node;
    private String shopCampaign;
    private Map<String, List<NavigationLinkDto>> navigationTree;
    private List<String> activeComponents;


}
