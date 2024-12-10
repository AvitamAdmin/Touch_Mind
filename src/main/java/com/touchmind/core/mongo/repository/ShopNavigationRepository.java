package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.ShopNavigation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ShopNavigationRepository")
public interface ShopNavigationRepository extends MongoRepository<ShopNavigation, ObjectId> {
    List<ShopNavigation> findBySubsidiaryAndSiteAndVariantAndShopCampaignOrderByCreationTimeDesc(String subsidiary, String site, String variant, String shopCampaign);

    List<ShopNavigation> findByNodeOrderByCreationTimeDesc(String node);

    ShopNavigation findByIdentifier(String identifier);

    List<ShopNavigation> findByShopCampaign(String shopCampaign);

    void deleteByRecordId(String recordId);

    List<ShopNavigation> findByStatusOrderByIdentifier(boolean b);

    ShopNavigation findByRecordId(String recordId);
}
