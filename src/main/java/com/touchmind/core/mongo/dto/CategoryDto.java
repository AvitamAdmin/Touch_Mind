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
public class CategoryDto extends CommonDto {
    private String parentId;
    private List<String> childIds;
    private Set<String> subsidiaries;
}
