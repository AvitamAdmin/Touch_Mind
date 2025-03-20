package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LocatorGroupWsDto extends CommonWsDto {
    private List<LocatorGroupDto> groupsDtoList;
}
