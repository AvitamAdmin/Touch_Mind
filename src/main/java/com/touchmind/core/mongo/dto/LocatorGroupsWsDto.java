package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LocatorGroupsWsDto extends CommonWsDto {
    private List<LocatorGroupsDto> groupsDtoList;
}
