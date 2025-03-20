package com.touchMind.core.service;

import com.touchMind.core.mongo.model.DataSource;

import java.io.File;
import java.util.Map;

public interface XmlFileService {

    boolean processXmlData(File file, DataSource dataSource, Map<String, String> primaryKeys);
}
