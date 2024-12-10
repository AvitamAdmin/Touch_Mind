package com.touchmind.core.service;

//import com.touchmind.form.CrawlerPathForm;

import com.touchmind.core.mongo.dto.CrawlerPathWsDto;


public interface CrawlerPathService {

    CrawlerPathWsDto handleEdit(CrawlerPathWsDto crawlerPathWsDto);
}
