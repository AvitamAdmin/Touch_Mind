package com.cheil.core.service;

import com.cheil.core.mongo.model.DataSource;

import java.io.File;
import java.util.Map;

public interface CsvFileService {
    void processCsv(File file, DataSource dataSource, Map<String, String> primaryKeys);
}
