package com.touchmind.fileimport.service.impl;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.touchmind.core.mongo.dto.CommonWsDto;
import com.touchmind.fileimport.service.FileImportService;
import com.touchmind.fileimport.strategies.FileImportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileImportServiceImpl implements FileImportService {

    public static final String OPEN_PARENTHESIS = "(";
    public static final String CLOSE_PARENTHESIS = ")";
    public static final String EQUAL = "=";
    public static final String REGULAR_EXPRESSION = "\\s*,\\s*";
    Logger logger = LoggerFactory.getLogger(FileImportServiceImpl.class);

    @Autowired
    private FileImportFactory fileImportFactory;

    @Override
    public Boolean importFile(MultipartFile file, String entityType, String entityName, String modelName, CommonWsDto commonWsDto) throws IOException {

        CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(',')
                .withIgnoreQuotations(true)
                .build();

        CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                .withSkipLines(0)
                .withCSVParser(csvParser)
                .build();

        String[] nextLine;
        Map<String, Map<String, String>> headersMap = new HashMap<>();
        List<String> headers = null;
        boolean isHeader = true;
        Boolean isSuccess = true;
        try {
            while ((nextLine = csvReader.readNext()) != null) {
                if (nextLine != null) {
                    if (isHeader) {
                        headers = Arrays.asList(nextLine);
                        headersMap = getHeaderAttributes(headers);
                        isHeader = false;
                        //  fileName = fileImportFactory.validate(headers, entityName);
                    } else {
                        List<String> values = Arrays.asList(nextLine);
                        if (values.size() != headers.size()) {
                            logger.error(values + " is does not natch to header hence ignoring the record !");
                        } else {
                            fileImportFactory.processRow(getRowMap(headers, values, headersMap), entityType, entityName, modelName, commonWsDto);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            isSuccess = false;
            logger.error(ex.getMessage());
        }
        fileImportFactory.getExtentManager(entityType).flush();
        return isSuccess;
    }

    private Map<String, Map<String, String>> getHeaderAttributes(List<String> headers) {
        Map<String, Map<String, String>> fieldAttributes = new HashMap<>();
        headers.stream().forEach(header -> {
            if (header.contains(OPEN_PARENTHESIS) && header.contains(CLOSE_PARENTHESIS)) {
                String attributes = header.substring(header.indexOf(OPEN_PARENTHESIS) + 1, header.indexOf(CLOSE_PARENTHESIS));
                fieldAttributes.put(header, getAttributeMap(attributes));
            }
        });
        return fieldAttributes;
    }

    private Map<String, String> getAttributeMap(String attributes) {
        List<String> items = Arrays.asList(attributes.split(REGULAR_EXPRESSION));
        Map<String, String> att = new HashMap<>();
        items.stream().forEach(attribute -> {
            String[] keyValue = attribute.split(EQUAL);
            att.put(keyValue[0], keyValue[1]);
        });
        return att;
    }

    private Map<String, EntityField> getRowMap(List<String> headers, List<String> values, Map<String, Map<String, String>> attributeMap) {
        Map<String, EntityField> rowMap = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            EntityField entityField = new EntityField();
            String header = headers.get(i);
            entityField.setAttributes(attributeMap.get(header));
            entityField.setValue(values.get(i));
            rowMap.put(header.contains(OPEN_PARENTHESIS) ? header.substring(0, header.indexOf(OPEN_PARENTHESIS)) : header, entityField);
        }
        return rowMap;
    }
}