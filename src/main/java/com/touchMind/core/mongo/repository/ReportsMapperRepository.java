package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.ReportsMapper;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ReportsMapperRepository")
public interface ReportsMapperRepository extends GenericImportRepository<ReportsMapper> {
    List<ReportsMapper> findBySubsidiaryAndSiteAndCampaignOrderByPriorityDesc(String subsidiary, String site, String campaign);

    List<ReportsMapper> findByStatusOrderByIdentifier(Boolean status);

    ReportsMapper findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);
}
