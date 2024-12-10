package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.SiteDto;
import com.touchmind.core.mongo.dto.SiteWsDto;
import com.touchmind.core.mongo.model.Site;
import com.touchmind.core.mongo.model.Subsidiary;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.SiteRepository;
import com.touchmind.core.mongo.repository.SubsidiaryRepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.SiteService;
import com.touchmind.core.service.SubsidiaryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SiteServiceImpl implements SiteService {

    public static final String ADMIN_SITE = "/admin/site";
    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private SubsidiaryService subsidiaryService;

    @Autowired
    private CoreService coreService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SubsidiaryRepository subsidiaryRepository;

    @Autowired
    private BaseService baseService;

    @Override
    public List<Site> findByStatusOrderBySiteId(Boolean status) {
        return siteRepository.findByStatusOrderByIdentifier(status);
    }

    @Override
    public List<Site> findBySubsidiaryId(Subsidiary subsidiary) {
        return siteRepository.findBySubsidiaryAndStatusOrderByIdentifier(subsidiary.getRecordId(), true);
    }

    @Override
    public List<Site> findBySubsidiaryAndStatusOrderBySiteId(Boolean status) {
        List<Subsidiary> subsidiaryList = subsidiaryService.findByStatusAndUserOrderByIdentifier(true);
        List<Site> sites = new ArrayList<>();
        for (Subsidiary subsidiary : subsidiaryList) {
            //TODO check if this fetch the record correctly
            sites.addAll(findBySubsidiaryId(subsidiary));
        }
        return sites;
    }

    @Override
    public SiteWsDto handleEdit(SiteWsDto request) {
        SiteWsDto siteWsDto = new SiteWsDto();
        Site requestData = null;
        List<SiteDto> sites = request.getSites();
        List<Site> siteList = new ArrayList<>();
        for (SiteDto site : sites) {
            if (site.getRecordId() != null) {
                requestData = siteRepository.findByRecordId(site.getRecordId());
                modelMapper.map(site, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.SITE, site.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(site, Site.class);
            }
            baseService.populateCommonData(requestData);
            siteRepository.save(requestData);
            if (site.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            siteRepository.save(requestData);
            siteList.add(requestData);
            siteWsDto.setBaseUrl(ADMIN_SITE);
        }
        siteWsDto.setSites(modelMapper.map(siteList, List.class));
        return siteWsDto;
    }
}
