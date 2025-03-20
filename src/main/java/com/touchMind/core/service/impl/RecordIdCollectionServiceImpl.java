package com.touchMind.core.service.impl;

import com.touchMind.core.HotFolderConstants;
import com.touchMind.core.mongo.model.QaResultReport;
import com.touchMind.core.mongo.repository.QaResultReportRepository;
import com.touchMind.core.mongotemplate.QATestResult;
import com.touchMind.core.mongotemplate.repository.QARepository;
import com.touchMind.core.service.RecordIdCollectionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RecordIdCollectionServiceImpl implements RecordIdCollectionService {

    public static final String EXTENSION = ".xlsx";
    public static final String SEPERATOR = "_";
    final String baseDir = HotFolderConstants.DOM_TREES + File.separator;
    final DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
    @Autowired
    private QaResultReportRepository qaResultReportRepository;
    @Autowired
    private QARepository qaRepository;

    @Override
    public String getDuplicateQaRecords(String entity) {

        List<QATestResult> qaTestResults = qaRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        if (CollectionUtils.isNotEmpty(qaTestResults)) {
            List<String> uniqueRecords = new ArrayList<>();
            Sheet sheet = workbook.createSheet("QaTestResult");
            AtomicInteger rowCount = new AtomicInteger(0);
            Row headerRow = sheet.createRow(rowCount.get());
            headerRow.createCell(0).setCellValue("Id");
            headerRow.createCell(1).setCellValue("recordId");
            qaTestResults.forEach(tree -> {
                if (!uniqueRecords.contains(tree.getIdentifier())) {
                    uniqueRecords.add(tree.getIdentifier());
                } else {
                    rowCount.getAndIncrement();
                    Row duplicateRow = sheet.createRow(rowCount.get());
                    duplicateRow.createCell(0).setCellValue(tree.getId().toString());
                    duplicateRow.createCell(1).setCellValue(tree.getIdentifier());
                }
            });
        }
        List<QaResultReport> qaResultReports = qaResultReportRepository.findAll();
        if (CollectionUtils.isNotEmpty(qaResultReports)) {
            List<String> uniqueRecords = new ArrayList<>();
            Sheet sheet = workbook.createSheet("QaResultReport");
            AtomicInteger rowCount = new AtomicInteger(0);
            Row headerRow = sheet.createRow(rowCount.get());
            headerRow.createCell(0).setCellValue("Id");
            headerRow.createCell(1).setCellValue("recordId");
            qaResultReports.forEach(tree -> {
                if (!uniqueRecords.contains(tree.getIdentifier())) {
                    uniqueRecords.add(tree.getIdentifier());
                } else {
                    rowCount.getAndIncrement();
                    Row duplicateRow = sheet.createRow(rowCount.get());
                    duplicateRow.createCell(0).setCellValue(tree.getId().toString());
                    duplicateRow.createCell(1).setCellValue(tree.getIdentifier());
                }
            });
        }
        String fileName = "duplicate_record_report" + SEPERATOR + df.format(new Date()) + EXTENSION;
        createFile(workbook, baseDir + fileName);
        return fileName;
    }

    @Override
    public void repairDuplicateRecords() {
        List<QATestResult> qaTestResults = qaRepository.findAll();
        if (CollectionUtils.isNotEmpty(qaTestResults)) {
            List<String> uniqueRecords = new ArrayList<>();
            qaTestResults.forEach(tree -> {
                if (!uniqueRecords.contains(tree.getIdentifier())) {
                    uniqueRecords.add(tree.getIdentifier());
                } else {
                    tree.setIdentifier(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                    qaRepository.save(tree);
                }
            });
        }
        List<QaResultReport> qaResultReports = qaResultReportRepository.findAll();
        if (CollectionUtils.isNotEmpty(qaResultReports)) {
            List<String> uniqueRecords = new ArrayList<>();
            qaResultReports.forEach(tree -> {
                if (!uniqueRecords.contains(tree.getIdentifier())) {
                    uniqueRecords.add(tree.getIdentifier());
                } else {
                    tree.setIdentifier(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                    qaResultReportRepository.save(tree);
                }
            });
        }
    }

    private void createFile(Workbook workbook, String fileName) {
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



