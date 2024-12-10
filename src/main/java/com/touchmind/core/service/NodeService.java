package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.NodeDto;
import com.touchmind.core.mongo.dto.NodeWsDto;
import com.touchmind.core.mongo.model.Node;

import java.util.List;

public interface NodeService {
    List<NodeDto> getAllNodes();

    List<NodeDto> getNodesForRoles();

    NodeWsDto handleEdit(NodeWsDto request);

    Node findById(String id);
}
