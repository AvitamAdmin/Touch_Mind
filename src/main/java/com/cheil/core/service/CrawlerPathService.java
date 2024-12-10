package com.cheil.core.service;

//import com.cheil.form.CrawlerPathForm;

import com.cheil.core.mongo.dto.CrawlerPathWsDto;


public interface CrawlerPathService {

    CrawlerPathWsDto handleEdit(CrawlerPathWsDto crawlerPathWsDto);
}
