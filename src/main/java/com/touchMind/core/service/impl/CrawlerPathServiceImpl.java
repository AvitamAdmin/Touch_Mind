package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.CrawlerPathDto;
import com.touchMind.core.mongo.dto.CrawlerPathWsDto;
import com.touchMind.core.mongo.model.CrawlerPath;
import com.touchMind.core.mongo.repository.CrawlerPathRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.CrawlerPathService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class CrawlerPathServiceImpl implements CrawlerPathService {

    public static final String ADMIN_CAMPAIGN = "/admin/campaigns";

    @Autowired
    private CrawlerPathRepository crawlerPathRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CoreService coreService;

    @Autowired
    private BaseService baseService;


    @Override
    public CrawlerPathWsDto handleEdit(CrawlerPathWsDto request) {
        List<CrawlerPath> updatedList = new ArrayList<>();
        CrawlerPath crawlerPath = null;
        for (CrawlerPathDto crawlerPathDto : request.getListPaths()) {
            if (crawlerPathDto.isAdd() && baseService.validateIdentifier(EntityConstants.CRAWLER_PATH, crawlerPathDto.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            crawlerPath = crawlerPathRepository.findByIdentifier(crawlerPathDto.getIdentifier());
            if (crawlerPath != null) {
                modelMapper.map(crawlerPathDto, crawlerPath);
            } else {

                crawlerPath = modelMapper.map(crawlerPathDto, CrawlerPath.class);
            }
            baseService.populateCommonData(crawlerPath);
            crawlerPathRepository.save(crawlerPath);
            updatedList.add(crawlerPath);
            request.setBaseUrl(ADMIN_CAMPAIGN);
            request.setMessage("CrawlerPath updated successfully");
        }
        Type listType = new TypeToken<List<CrawlerPathDto>>() {
        }.getType();
        request.setMessage("Crawler path updated successfully!!");
        request.setListPaths(modelMapper.map(updatedList, listType));
        return request;
    }
}


