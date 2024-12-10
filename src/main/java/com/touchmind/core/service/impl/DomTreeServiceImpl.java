package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.ShopNavigationDto;
import com.touchmind.core.mongo.dto.ShopNavigationWsDto;
import com.touchmind.core.mongo.model.ShopNavigation;
import com.touchmind.core.mongo.repository.ShopNavigationRepository;
import com.touchmind.core.service.DomTreeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DomTreeServiceImpl implements DomTreeService {

    @Autowired
    private ShopNavigationRepository shopNavigationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ShopNavigationWsDto editCategory(ShopNavigationWsDto shopNavigationWsDto) {
        List<ShopNavigationDto> updatedShopNavigationList = new ArrayList<>();

        for (ShopNavigationDto shopNavigationDto : shopNavigationWsDto.getShopNavigationList()) {
            ShopNavigation shopNavigation = shopNavigationRepository.findByRecordId(shopNavigationDto.getRecordId());
            if (shopNavigation != null) {
                ShopNavigationDto mappedDto = modelMapper.map(shopNavigation, ShopNavigationDto.class);
                updatedShopNavigationList.add(mappedDto);
            } else {
                updatedShopNavigationList.add(shopNavigationDto);
            }
        }

        shopNavigationWsDto.setShopNavigationList(updatedShopNavigationList);
        return shopNavigationWsDto;
    }


}
