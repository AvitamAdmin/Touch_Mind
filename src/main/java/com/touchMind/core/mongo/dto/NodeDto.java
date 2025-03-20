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
public class NodeDto extends CommonDto {
    private String path;
    private NodeDto parentNode;
    private Integer displayPriority;
    private List<NodeDto> childNodes;
}
