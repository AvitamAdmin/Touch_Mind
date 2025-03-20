package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.ReportCompiler;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ReportCompilerRepository")
public interface ReportCompilerRepository extends GenericImportRepository<ReportCompiler> {
    ReportCompiler findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    ReportCompiler findByNode(String node);

    List<ReportCompiler> findByStatusOrderByIdentifier(boolean b);

    List<ReportCompiler> findAllByOrderByIdentifier();
}
