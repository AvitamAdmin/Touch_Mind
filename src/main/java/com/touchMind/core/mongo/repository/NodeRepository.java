package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.Node;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("NodeRepository")
public interface NodeRepository extends GenericImportRepository<Node> {
    List<Node> findByParentNode(Node parentNode);

    List<Node> findByParentNodeAndStatus(Node parentNode, boolean status);

    Node findByPath(String path);

    Node findByIdentifier(String path);

    List<Node> findByStatusOrderById(Boolean status);

    List<Node> findByStatusOrderByDisplayPriority(Boolean status);

    void deleteByIdentifier(String valueOf);

}
