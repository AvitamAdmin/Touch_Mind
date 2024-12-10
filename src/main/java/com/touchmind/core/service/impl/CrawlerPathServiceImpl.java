package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.CrawlerPathDto;
import com.touchmind.core.mongo.dto.CrawlerPathWsDto;
import com.touchmind.core.mongo.model.CrawlerPath;
import com.touchmind.core.mongo.repository.CrawlerPathRepository;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.CrawlerPathService;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CrawlerPathServiceImpl implements CrawlerPathService {

    @Autowired
    private CrawlerPathRepository crawlerPathRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CoreService coreService;


    @Override
    public CrawlerPathWsDto handleEdit(CrawlerPathWsDto crawlerPathWsDto) {
        List<CrawlerPathDto> updatedList = new ArrayList<>();
        for (CrawlerPathDto crawlerPathDto : crawlerPathWsDto.getListPaths()) {
            CrawlerPath crawlerPath;
            if (StringUtils.isNotEmpty(crawlerPathDto.getRecordId())) {
                crawlerPath = crawlerPathRepository.findByRecordId(crawlerPathDto.getRecordId());
                if (crawlerPath != null) {
                    modelMapper.map(crawlerPathDto, crawlerPath);
                    crawlerPath.setLastModified(new Date());
                } else {
                    throw new RuntimeException("No CrawlerPath found with recordId: " + crawlerPathDto.getRecordId());
                }
            } else {
                crawlerPath = new CrawlerPath();
                modelMapper.map(crawlerPathDto, crawlerPath);
                crawlerPath.setCreationTime(new Date());
                crawlerPath.setCreator(coreService.getCurrentUser().getUsername());
            }
            crawlerPathRepository.save(crawlerPath);
            if (StringUtils.isEmpty(crawlerPath.getRecordId())) {
                crawlerPath.setRecordId(String.valueOf(crawlerPath.getId().getTimestamp()));
                crawlerPathRepository.save(crawlerPath);
            }
            crawlerPathDto.setRecordId(crawlerPath.getRecordId());
            CrawlerPathDto updatedDto = modelMapper.map(crawlerPath, CrawlerPathDto.class);
            updatedList.add(updatedDto);
        }
        crawlerPathWsDto.setListPaths(updatedList);
        return crawlerPathWsDto;
    }
}
