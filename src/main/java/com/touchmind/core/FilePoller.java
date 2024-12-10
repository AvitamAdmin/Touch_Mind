package com.touchmind.core;

import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.FileListFilter;

import java.io.File;

public class FilePoller extends FileReadingMessageSource {

    public FilePoller(File directory, FileListFilter<File> filter) {
        super.setDirectory(directory);
        super.setFilter(filter);
        super.setUseWatchService(true);
    }
}