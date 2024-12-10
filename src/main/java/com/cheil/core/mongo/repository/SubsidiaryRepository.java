package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.Subsidiary;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SubsidiaryRepository")
public interface SubsidiaryRepository extends GenericImportRepository<Subsidiary> {
    Subsidiary findByIdentifier(String subId);

    List<Subsidiary> findByStatusOrderByIdentifier(Boolean status);

    Subsidiary findByRecordId(String recordId);

    void deleteByRecordId(String valueOf);

}
