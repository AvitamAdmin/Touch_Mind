package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.InterfaceConfig;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("InterfaceConfigRepository")
public interface InterfaceConfigRepository extends GenericImportRepository<InterfaceConfig> {
    InterfaceConfig findByRecordId(String id);

    InterfaceConfig findByNode(String node);

    List<InterfaceConfig> findByStatusOrderByIdentifier(boolean status);

    List<InterfaceConfig> findAllByOrderByIdentifier();

    void deleteByRecordId(String id);
}
