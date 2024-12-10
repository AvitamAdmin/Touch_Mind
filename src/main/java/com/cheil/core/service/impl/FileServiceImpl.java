package com.cheil.core.service.impl;

import com.cheil.core.mongotemplate.QATestResult;
import com.cheil.core.mongotemplate.repository.QARepository;
import com.cheil.core.service.FileService;
import com.cheil.qa.framework.ExtentManager;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private QARepository qaRepository;

    @Override
    public void deleteFilesByQaResult(QATestResult qaResult) {
        if (qaResult != null) {
            //Delete screenshots
            List<File> files = getFiles(ExtentManager.REPORT_PATH + File.separator + "screenshots", qaResult.getLocatorGroupIdentifier() + "*_" + qaResult.getSessionId() + "_*");
            files.forEach(file -> {
                if (file != null && file.isFile()) {
                    deleteFile(file);
                }
            });
            // Delete html files
            files = getFiles(ExtentManager.REPORT_PATH, qaResult.getLocatorGroupIdentifier() + "*_" + qaResult.getSessionId() + "*");
            files.forEach(file -> {
                if (file != null && file.isFile()) {
                    deleteFile(file);
                }
            });
        }
        qaRepository.delete(qaResult);
    }

    @Override
    public void deleteFilesByQaResultId(String id) {
        //TODO
        //QATestResult qaRepositoryOp = qaRepository.findByRecordId(id);
        QATestResult qaRepositoryOp = null;
        if (qaRepositoryOp != null) {
            deleteFilesByQaResult(qaRepositoryOp);
        }
        //finally delete QA result
        qaRepository.deleteById(id);
    }

    @Override
    public List<File> getFiles(String path, String wildCardFilter) {
        File dir = new File(path);
        FileFilter fileFilter = new WildcardFileFilter(wildCardFilter);
        File[] files = dir.listFiles(fileFilter);
        return Arrays.stream(files).collect(Collectors.toList());
    }

    @Override
    public boolean deleteFile(File file, String deleteDays) {
        if (isFileOld(file, deleteDays)) {
            file.delete();
        }
        return false;
    }

    @Override
    public boolean deleteFile(File file) {
        return file.delete();
    }

    private boolean isFileOld(File file, String deleteDays) {
        if (StringUtils.isEmpty(deleteDays)) {
            LOG.error("No property defined for delete files number of days , please check the application property configured correctly");
            return false;
        }
        LocalDate fileDate = Instant.ofEpochMilli(file.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate oldDate = LocalDate.now().minusDays(Long.parseLong(deleteDays));
        return fileDate.isBefore(oldDate);
    }
}
