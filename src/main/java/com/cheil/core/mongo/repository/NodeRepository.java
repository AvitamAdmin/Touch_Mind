package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.Node;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("NodeRepository")
public interface NodeRepository extends GenericImportRepository<Node> {
    List<Node> findByParentNode(Node parentNode);

    Node findByPath(String path);

    Node findByIdentifier(String path);

    List<Node> findByStatusOrderById(Boolean status);

    List<Node> findByStatusOrderByDisplayPriority(Boolean status);

    Node findByRecordId(String nodeId);

    void deleteByRecordId(String valueOf);

}
