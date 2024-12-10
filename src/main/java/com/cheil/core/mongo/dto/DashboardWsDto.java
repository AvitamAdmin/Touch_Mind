package com.cheil.core.mongo.dto;

import com.cheil.core.mongo.model.Subsidiary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DashboardWsDto extends CommonWsDto {
    List<Subsidiary> subsidiaries;
    private List<DashboardDto> dashboards;
}
