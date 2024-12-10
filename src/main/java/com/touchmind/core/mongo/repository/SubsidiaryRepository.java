package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.Subsidiary;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SubsidiaryRepository")
public interface SubsidiaryRepository extends GenericImportRepository<Subsidiary> {
    Subsidiary findByIdentifier(String subId);

    List<Subsidiary> findByStatusOrderByIdentifier(Boolean status);

    Subsidiary findByRecordId(String recordId);

    void deleteByRecordId(String valueOf);

}
