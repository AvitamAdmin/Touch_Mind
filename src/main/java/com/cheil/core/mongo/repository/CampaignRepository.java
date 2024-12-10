package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.Campaign;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CampaignRepository")
public interface CampaignRepository extends MongoRepository<Campaign, ObjectId> {
    Campaign findByRecordId(String recordId);

    List<Campaign> findByStatusOrderByIdentifier(boolean b);

    Campaign findByStatusAndRecordIdOrderByIdentifier(boolean b, Long shopCampaign);

    void deleteByRecordId(String recordId);
}
