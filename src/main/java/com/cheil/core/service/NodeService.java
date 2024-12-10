package com.cheil.core.service;

import com.cheil.core.mongo.dto.NodeDto;
import com.cheil.core.mongo.dto.NodeWsDto;
import com.cheil.core.mongo.model.Node;

import java.util.List;

public interface NodeService {
    List<NodeDto> getAllNodes();

    List<NodeDto> getNodesForRoles();

    NodeWsDto handleEdit(NodeWsDto request);

    Node findById(String id);
}
