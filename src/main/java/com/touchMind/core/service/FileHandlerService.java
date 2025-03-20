package com.touchMind.core.service;

import com.touchMind.core.mongo.model.DataSource;

import java.io.File;

public interface FileHandlerService {
    DataSource getDataSourceForFileName(File file);
}
