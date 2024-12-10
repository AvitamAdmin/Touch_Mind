package com.touchmind.core.service;

import com.touchmind.core.mongo.model.DataSource;

import java.io.File;

public interface FileHandlerService {
    DataSource getDataSourceForFileName(File file);
}
