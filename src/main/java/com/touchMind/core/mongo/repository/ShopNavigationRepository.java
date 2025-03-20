package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.ShopNavigation;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ShopNavigationRepository")
public interface ShopNavigationRepository extends GenericImportRepository<ShopNavigation> {
    List<ShopNavigation> findBySubsidiaryAndSiteAndVariantAndShopCampaignOrderByCreationTimeDesc(String subsidiary, String site, String variant, String shopCampaign);

    List<ShopNavigation> findByNodeOrderByCreationTimeDesc(String node);

    ShopNavigation findByIdentifier(String identifier);

    List<ShopNavigation> findByShopCampaign(String shopCampaign);

    void deleteByIdentifier(String identifier);

    List<ShopNavigation> findByStatusOrderByIdentifier(boolean b);

    List<ShopNavigation> findByShopCampaignAndSite(String campaign, String id);

}
