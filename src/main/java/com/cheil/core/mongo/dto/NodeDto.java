package com.cheil.core.mongo.dto;

import com.cheil.core.mongo.model.Node;
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
    private Node parentNode;
    private Integer displayPriority;
    private List<NodeDto> childNodes;
}
