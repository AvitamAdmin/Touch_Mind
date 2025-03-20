package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.SiteDto;
import com.touchMind.core.mongo.dto.SiteWsDto;
import com.touchMind.core.mongo.model.Site;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.SiteRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.SiteService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class SiteServiceImpl implements SiteService {

    public static final String ADMIN_SITE = "/admin/site";
    @Autowired
    private SiteRepository siteRepository;
//
//    @Autowired
//    private SubsidiaryService subsidiaryService;

    @Autowired
    private CoreService coreService;

    @Autowired
    private ModelMapper modelMapper;

//    @Autowired
//    private SubsidiaryRepository subsidiaryRepository;

    @Autowired
    private BaseService baseService;

    @Override
    public List<Site> findByStatusOrderBySiteId(Boolean status) {
        return siteRepository.findByStatusOrderByIdentifier(status);
    }

//    @Override
//    public List<Site> findBySubsidiaryId(Subsidiary subsidiary) {
//        return siteRepository.findBySubsidiaryAndStatusOrderByIdentifier(subsidiary.getIdentifier(), true);
//    }

    @Override
    public List<Site> findBySubsidiaryAndStatusOrderBySiteId(Boolean status) {
      //  List<Subsidiary> subsidiaryList = subsidiaryService.findByStatusAndUserOrderByIdentifier(true);
        List<Site> sites = new ArrayList<>();
//        for (Subsidiary subsidiary : subsidiaryList) {
//            //TODO check if this fetch the record correctly
//            sites.addAll(findBySubsidiaryId(subsidiary));
//        }
        return sites;
    }

    @Override
    public SiteWsDto handleEdit(SiteWsDto request) {
        SiteWsDto siteWsDto = new SiteWsDto();
        Site requestData = null;
        List<SiteDto> sites = request.getSites();
        List<Site> siteList = new ArrayList<>();
        for (SiteDto site : sites) {
            if (site.isAdd() && baseService.validateIdentifier(EntityConstants.SITE, site.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = siteRepository.findByIdentifier(site.getIdentifier());
            if (requestData != null) {
                modelMapper.map(site, requestData);
            } else {
                requestData = modelMapper.map(site, Site.class);
            }
            baseService.populateCommonData(requestData);
            siteRepository.save(requestData);
            siteList.add(requestData);
            siteWsDto.setBaseUrl(ADMIN_SITE);
        }
        siteWsDto.setMessage("Site updated successfully");
        Type listType = new TypeToken<List<SiteDto>>() {
        }.getType();
        siteWsDto.setSites(modelMapper.map(siteList, listType));
        return siteWsDto;
    }
}
