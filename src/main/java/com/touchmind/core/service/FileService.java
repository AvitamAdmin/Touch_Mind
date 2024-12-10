package com.touchmind.core.service;

import com.touchmind.core.mongotemplate.QATestResult;

import java.io.File;
import java.util.List;

public interface FileService {
    void deleteFilesByQaResult(QATestResult qaResult);

    void deleteFilesByQaResultId(String id);

    List<File> getFiles(String path, String wildCardFilter);

    boolean deleteFile(File file, String deleteDays);

    boolean deleteFile(File file);
}
