package com.cheil.core.mongo.dto;

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
public class DashboardProfileWsDto extends CommonWsDto {
    private List<DashboardProfileDto> dashboardProfiles;
    private Set<String> dashboardLabels;
}
