package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.ReportCompiler;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ReportCompilerRepository")
public interface ReportCompilerRepository extends GenericImportRepository<ReportCompiler> {
    ReportCompiler findByIdentifier(String identifier);

    ReportCompiler findByRecordId(String recordId);

    void deleteByRecordId(String recordId);

    ReportCompiler findByNode(String node);

    List<ReportCompiler> findByStatusOrderByIdentifier(boolean b);

    List<ReportCompiler> findAllByOrderByIdentifier();
}
