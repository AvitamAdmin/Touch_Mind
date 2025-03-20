package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.NodeDto;
import com.touchMind.core.mongo.dto.NodeWsDto;
import com.touchMind.core.mongo.model.Node;

import java.util.List;

public interface NodeService {
    List<NodeDto> getAllNodes();

    List<NodeDto> getNodesForRoles();

    NodeWsDto handleEdit(NodeWsDto request);

    Node findById(String id);
}
