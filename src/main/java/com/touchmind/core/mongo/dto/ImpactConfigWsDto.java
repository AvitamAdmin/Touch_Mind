package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ImpactConfigWsDto extends CommonWsDto {
    Set<String> dashboardLabels;
    private List<ImpactConfigDto> impactConfigs;
    private int existingLabelCount;
}
