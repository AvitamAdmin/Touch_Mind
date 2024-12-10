package com.touchmind.core.service;

import com.touchmind.core.HotFolderConstants;
import com.touchmind.core.mongo.model.DataSource;
import com.touchmind.core.mongo.model.baseEntity.BaseEntity;
import com.touchmind.core.mongo.repository.generic.GenericRepository;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class
ExcelFileService implements HotFolderConstants {

    private static String rootFolder = null;
    Logger logger = LoggerFactory.getLogger(ExcelFileService.class);
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private Environment env;

    public void processExcelData(File file, DataSource dataSource, Map<String, String> primaryKeys) {

        if (rootFolder == null) {
            rootFolder = ServiceUtil.getProcessedFolderLocation(env);
        }

        Optional<String> optionalEntityId = primaryKeys.keySet().stream().findFirst();
        if (!optionalEntityId.isPresent()) {
            logger.error("No mapping found for entity hence ignoring the file for further processing : " + file.getName());
            ServiceUtil.moveFile(file, rootFolder, UNSUPPORTED);
            return;
        }

        String entityId = optionalEntityId.get();

        GenericRepository genericRepository = repositoryService.getRepositoryForRelationId(entityId);

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(file);
        } catch (Exception e) {
            logger.error("Invalid file format ignoring the file " + file.getName() + e);
            ServiceUtil.moveFile(file, rootFolder, ERROR);
        }
        XSSFSheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);
        Collection<String> primaryValues = primaryKeys.values();

        Integer primaryKeyCellNumber = null;
        Iterator<Row> rows = sheet.rowIterator();

        //Process headxer and get primary key
        primaryKeyCellNumber = getPrimaryKeyCellNumber(headerRow, primaryValues);
        if (primaryKeyCellNumber == null) {
            logger.error("No primary key found to process hence ignring the file " + file.getName());
            ServiceUtil.moveFile(file, rootFolder, ERROR);
            return;
        }
        int rowCounter = 0;
        int updateCount = 0;
        boolean isHeader = true;
        while (rows.hasNext()) {
            if (isHeader) {
                rows.next();
                isHeader = false;
                continue;
            }
            Row currentRow = rows.next();
            String value = currentRow.getCell(primaryKeyCellNumber).getStringCellValue();
            List<BaseEntity> entities = genericRepository.findByRecordIdAndRelationIdAndSessionId(value, entityId, dataSource.getIdentifier());
            Optional<BaseEntity> baseEntityOptional = entities.stream().findFirst();
            Map<String, Object> record = null;
            BaseEntity baseEntity = null;
            if (baseEntityOptional.isPresent()) {
                baseEntity = baseEntityOptional.get();
                updateCount++;
                record = baseEntity.getRecords();
            } else {
                baseEntity = repositoryService.getNewEntityForName(entityId);
                baseEntity.setSessionId(dataSource.getIdentifier());
                baseEntity.setId(new ObjectId().get());
                baseEntity.setRecordId(value);
                baseEntity.setRelationId(primaryKeys.keySet().stream().findFirst().get());
                record = new HashMap<>();
            }
            Iterator<Cell> cellIterator = headerRow.cellIterator();
            int cellCount = 0;
            while (cellIterator.hasNext()) {
                Cell headerCell = cellIterator.next();
                String key = headerCell.getStringCellValue().stripTrailing().stripLeading();
                if (dataSource.getSrcInputParams().contains(key)) {
                    record.put(key, ServiceUtil.getCellValue(currentRow.getCell(cellCount)));
                }
                cellCount++;
            }
            baseEntity.setRecords(record);
            rowCounter++;
            if (rowCounter % 100 == 0) {
                logger.info("Processing file " + file.getName() + " processed " + rowCounter + " rows");
            }
            genericRepository.save(baseEntity);
        }
        logger.info("Inserted " + (rowCounter - updateCount) + " rows");
        logger.info("Updated " + updateCount + " rows");
        logger.info("***** Successfully processed file " + file.getName() + " processed " + rowCounter + " rows *****");
        ServiceUtil.moveFile(file, rootFolder, ARCHIVE);
    }

    private Integer getPrimaryKeyCellNumber(Row currentRow, Collection<String> primaryValues) {
        Iterator<Cell> cellIterator = currentRow.cellIterator();
        int currentIndex = 0;
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (primaryValues.contains(cell.getStringCellValue().stripTrailing().stripLeading())) {
                return currentIndex;
            }
            currentIndex++;
        }
        return null;
    }

    public List<Map<String, String>> loadExcelData(MultipartFile file) throws IOException {
        InputStream inputStream = new BufferedInputStream(file.getInputStream());
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);

        XSSFSheet sheet = wb.getSheetAt(0);

        List<Map<String, String>> excelData = new ArrayList<>();
        int rowCount = sheet.getLastRowNum();
        for (int i = 1; i <= rowCount; i++) {
            boolean isEndChar = false;
            Row row = sheet.getRow(0);
            Map<String, String> rowData = new HashMap<>();
            for (int j = 0; j < row.getLastCellNum(); j++) {
                String key = row.getCell(j).getStringCellValue();
                XSSFCell val = sheet.getRow(i).getCell(j);
                String value = "";
                if (val != null) {
                    if (val.getCellType().getCode() == 0) {
                        value = String.valueOf(val.getRawValue());
                    } else {
                        value = String.valueOf(val.getStringCellValue());
                    }
                    if (value.equalsIgnoreCase("END")) {
                        isEndChar = true;
                    }
                    if (isEndChar && StringUtils.isEmpty(value)) {
                        value = "END";
                    }
                }
                if (value.equalsIgnoreCase("null")) {
                    value = "";
                }
                rowData.put(key, value);
            }
            excelData.add(rowData);
        }
        return excelData;
    }

    public void writeDataToExcel(File file, List<MultiValuedMap<String, String>> failedErrorMap) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Failed Skus");
        FileOutputStream out = null;
        int index = 0;
        for (MultiValuedMap<String, String> failedDataMap : failedErrorMap) {
            for (String mapKey : failedDataMap.keySet()) {
                Row row = sheet.createRow(index);
                if (mapKey.contains("|")) {
                    String[] mapArray = mapKey.split("\\|");
                    Cell cell = row.createCell(0);
                    cell.setCellValue(mapArray[0]);
                    Cell cell2 = row.createCell(1);
                    cell2.setCellValue(mapArray[1]);
                    Cell cell3 = row.createCell(2);
                    cell3.setCellValue(failedDataMap.get(mapKey).toString());
                } else {
                    Cell cell = row.createCell(0);
                    cell.setCellValue(mapKey);
                    Cell cell2 = row.createCell(1);
                    cell2.setCellValue(failedDataMap.get(mapKey).toString());
                }

                out = new FileOutputStream(
                        file);
                workbook.write(out);
                index++;
            }
        }
        out.close();
    }
}
