package com.cheil.core.service;

import com.cheil.core.mongo.model.DataSource;

import java.io.File;

public interface FileHandlerService {
    DataSource getDataSourceForFileName(File file);
}
