package com.touchmind.web.controllers.admin.domtree;

import com.touchmind.core.mongo.dto.ShopNavigationDto;
import com.touchmind.core.mongo.dto.ShopNavigationWsDto;
import com.touchmind.core.mongo.model.ShopNavigation;
import com.touchmind.core.mongo.repository.ShopNavigationRepository;
import com.touchmind.core.service.DomTreeService;
import com.touchmind.web.controllers.BaseController;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/domTree")
public class DomTreeController extends BaseController {

    public static final String ADMIN_DOM_TREE = "/admin/domTree";

    Logger logger = LoggerFactory.getLogger(DomTreeController.class);
    @Autowired
    private ShopNavigationRepository shopNavigationRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private DomTreeService domTreeService;

    @PostMapping
    @ResponseBody
    public ShopNavigationWsDto getAllTrees(@RequestBody ShopNavigationWsDto shopNavigationWsDto) {
        Pageable pageable = getPageable(shopNavigationWsDto.getPage(), shopNavigationWsDto.getSizePerPage(), shopNavigationWsDto.getSortDirection(), shopNavigationWsDto.getSortField());
        ShopNavigationDto shopNavigationDto = CollectionUtils.isNotEmpty(shopNavigationWsDto.getShopNavigationList()) ? shopNavigationWsDto.getShopNavigationList().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(shopNavigationDto, shopNavigationWsDto.getOperator());
        ShopNavigation shopNavigation = shopNavigationDto != null ? modelMapper.map(shopNavigationDto, ShopNavigation.class) : null;
        Page<ShopNavigation> page = isSearchActive(shopNavigation) != null ? shopNavigationRepository.findAll(Example.of(shopNavigation, exampleMatcher), pageable) : shopNavigationRepository.findAll(pageable);
        shopNavigationWsDto.setShopNavigationList(modelMapper.map(page.getContent(), List.class));
        shopNavigationWsDto.setBaseUrl(ADMIN_DOM_TREE);
        shopNavigationWsDto.setTotalPages(page.getTotalPages());
        shopNavigationWsDto.setTotalRecords(page.getTotalElements());
        shopNavigationWsDto.setAttributeList(getConfiguredAttributes(shopNavigationWsDto.getNode()));
        return shopNavigationWsDto;
    }

    @GetMapping("/get")
    @ResponseBody
    public ShopNavigationWsDto getActiveTrees() {
        ShopNavigationWsDto shopNavigationWsDto = new ShopNavigationWsDto();
        shopNavigationWsDto.setBaseUrl(ADMIN_DOM_TREE);
        shopNavigationWsDto.setShopNavigationList(modelMapper.map(shopNavigationRepository.findByStatusOrderByIdentifier(true), List.class));
        return shopNavigationWsDto;
    }

    @GetMapping("/edit")
    @ResponseBody
    public ShopNavigationWsDto editCategory(@RequestBody ShopNavigationWsDto shopNavigationWsDto) {
        return domTreeService.editCategory(shopNavigationWsDto);
    }

    @GetMapping("/{node}")
    @ResponseBody
    public ShopNavigationWsDto showDom(@RequestBody ShopNavigationWsDto shopNavigationWsDto) {
        ShopNavigationDto shopNavigationDto = new ShopNavigationDto();
        List<ShopNavigation> shopNavigationList = shopNavigationRepository.findByNodeOrderByCreationTimeDesc("/admin/domTree/" + shopNavigationDto.getNode());
        List<ShopNavigationDto> shopNavigationListDto = shopNavigationList.stream()
                .map(shopNavigation -> modelMapper.map(shopNavigation, ShopNavigationDto.class))
                .toList();
        shopNavigationWsDto.setShopNavigationList(shopNavigationListDto);
        return shopNavigationWsDto;
    }

    @PostMapping("/delete")
    public ShopNavigationWsDto deleteCategory(@RequestBody ShopNavigationWsDto shopNavigationWsDto) {
        for (ShopNavigationDto shopNavigationDto : shopNavigationWsDto.getShopNavigationList()) {
            shopNavigationRepository.deleteByRecordId(shopNavigationDto.getRecordId());
        }
        shopNavigationWsDto.setMessage("Data deleted successfully");
        return shopNavigationWsDto;
    }
}
