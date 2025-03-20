package com.touchMind.core.service;


import com.touchMind.core.mongo.dto.CrawlerPathWsDto;

public interface CrawlerPathService {

    CrawlerPathWsDto handleEdit(CrawlerPathWsDto crawlerPathWsDto);
}
