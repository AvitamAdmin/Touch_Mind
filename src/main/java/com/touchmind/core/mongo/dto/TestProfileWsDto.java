package com.touchmind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TestProfileWsDto extends CommonWsDto {
    List<ProfileLocatorDto> profileLocatorList;
    private List<TestProfileDto> testProfiles;
}
