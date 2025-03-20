package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.InterfaceConfig;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("InterfaceConfigRepository")
public interface InterfaceConfigRepository extends GenericImportRepository<InterfaceConfig> {
    InterfaceConfig findByIdentifier(String id);

    InterfaceConfig findByNode(String node);

    List<InterfaceConfig> findByStatusOrderByIdentifier(boolean status);

    List<InterfaceConfig> findAllByOrderByIdentifier();

    void deleteByIdentifier(String id);
}
