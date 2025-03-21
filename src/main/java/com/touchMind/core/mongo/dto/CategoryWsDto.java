package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CategoryWsDto extends CommonWsDto {
    private List<CategoryDto> categories;
    private List<String> childId;
}
