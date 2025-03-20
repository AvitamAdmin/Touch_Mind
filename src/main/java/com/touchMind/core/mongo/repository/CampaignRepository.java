package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.Campaign;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CampaignRepository")
public interface CampaignRepository extends GenericImportRepository<Campaign> {
    Campaign findByIdentifier(String identifier);

    List<Campaign> findByStatusOrderByIdentifier(boolean b);

    Campaign findByStatusAndIdentifierOrderByIdentifier(boolean b, String shopCampaign);

    void deleteByIdentifier(String identifier);
}
