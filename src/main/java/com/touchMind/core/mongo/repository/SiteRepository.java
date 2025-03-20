package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.Site;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SiteRepository")
public interface SiteRepository extends GenericImportRepository<Site> {
    List<Site> findBySubsidiaryAndStatusOrderByIdentifier(String subsidiary, Boolean b);

    List<Site> findByStatusOrderByIdentifier(Boolean status);

    void deleteByIdentifier(String valueOf);

    Site findByIdentifier(String identifier);

}
