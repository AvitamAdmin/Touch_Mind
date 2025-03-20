package com.touchMind.core.service.impl;

import com.touchMind.core.HotFolderConstants;
import com.touchMind.core.mongo.model.DataSource;
import com.touchMind.core.mongo.model.baseEntity.BaseEntity;
import com.touchMind.core.mongo.repository.generic.GenericRepository;
import com.touchMind.core.service.CsvFileService;
import com.touchMind.core.service.RepositoryService;
import com.touchMind.core.service.ServiceUtil;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

@Service
public class CsvFileServiceImpl implements CsvFileService, HotFolderConstants {

    private static String rootFolder = null;
    Logger logger = LoggerFactory.getLogger(CsvFileServiceImpl.class);
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private Environment env;

    @Override
    public void processCsv(File file, DataSource dataSource, Map<String, String> primaryKeys) {

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

        if (genericRepository == null) {
            logger.error("Unknown repository ignoring file for further processing" + file.getName());
            ServiceUtil.moveFile(file, rootFolder, ERROR);
            return;
        }

        CSVReader reader = null;
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build();
            reader = new CSVReaderBuilder(
                    new FileReader(file))
                    .withCSVParser(csvParser)
                    .build();

            Iterator<String[]> it = reader.iterator();
            String[] headerRow = it.next();
            Integer primaryKeyCellNumber = null;
            Collection<String> primaryValues = primaryKeys.values();

            primaryKeyCellNumber = getPrimaryKeyCellNumber(headerRow, primaryValues);

            if (primaryKeyCellNumber == null) {
                logger.error("No primary key found to process hence ignring the file " + file.getName());
                ServiceUtil.moveFile(file, rootFolder, ERROR);
                return;
            }
            while (it.hasNext()) {
                String[] currentRow = it.next();
                for (int i = 0; i < currentRow.length; i++) {
                    if (primaryKeyCellNumber == i) {
                        BaseEntity baseEntity = genericRepository.findByIdentifierAndRelationId(currentRow[i], primaryKeys.keySet().stream().findFirst().get());
                        Map<String, Object> record = null;
                        if (baseEntity == null) {
                            baseEntity = repositoryService.getNewEntityForName(entityId);
                            new ObjectId();
                            baseEntity.setId(ObjectId.get());
                            baseEntity.setIdentifier(currentRow[i]);
                            baseEntity.setRelationId(primaryKeys.keySet().stream().findFirst().get());
                            record = new HashMap<>();
                        } else {
                            record = baseEntity.getRecords();
                        }
                        for (int counter = 0; counter < currentRow.length; counter++) {
                            record.put(headerRow[counter], currentRow[counter]);
                        }
                        baseEntity.setRecords(record);
                        genericRepository.save(baseEntity);
                    }
                }
            }
            ServiceUtil.moveFile(file, rootFolder, ARCHIVE);
        } catch (FileNotFoundException e) {
            logger.error("Could not process File " + file.getName());
            ServiceUtil.moveFile(file, rootFolder, ERROR);
        }
    }

    private Integer getPrimaryKeyCellNumber(String[] headerRow, Collection<String> primaryValues) {
        int currentIndex = 0;
        Optional<String> optionalPrimaryKey = primaryValues.stream().findFirst();
        if (!optionalPrimaryKey.isPresent()) {
            return null;
        }
        String primaryKey = optionalPrimaryKey.get();

        for (String cell : headerRow) {
            if (primaryValues.contains(primaryKey)) {
                return currentIndex;
            }
            currentIndex++;
        }
        return null;
    }
}
