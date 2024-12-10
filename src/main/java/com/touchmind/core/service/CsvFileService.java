package com.touchmind.core.service;

import com.touchmind.core.mongo.model.DataSource;

import java.io.File;
import java.util.Map;

public interface CsvFileService {
    void processCsv(File file, DataSource dataSource, Map<String, String> primaryKeys);
}
