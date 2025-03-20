package com.touchMind.core.mongo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class RoleDto extends CommonDto {
    private String name;
    private Set<NodeDto> permissions;
    private String quota;
    private String quotaUsed;
    private Boolean published;
}
