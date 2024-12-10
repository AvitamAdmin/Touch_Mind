package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.Site;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SiteRepository")
public interface SiteRepository extends GenericImportRepository<Site> {
    List<Site> findBySubsidiaryAndStatusOrderByIdentifier(String subsidiary, Boolean b);

    List<Site> findByStatusOrderByIdentifier(Boolean status);

    Site findByRecordId(String valueOf);

    void deleteByRecordId(String valueOf);

}
