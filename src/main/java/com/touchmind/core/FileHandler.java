package com.touchmind.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class FileHandler implements MessageHandler, HotFolderConstants {

    private static String rootFolder = null;
    private final Map<String, String> fileFormatMap = new HashMap<>();
    Logger logger = LoggerFactory.getLogger(FileHandler.class);


//    @Autowired
//    private CsvFileService csvFileService;


    @Autowired
    private Environment env;

    private String outputLocation;

//    public void handleMessage(Message<?> mesg) throws MessagingException {
//
//        if (rootFolder == null) {
//            rootFolder = ServiceUtil.getProcessedFolderLocation(env);
//        }
//
//        File file = (File) mesg.getPayload();
//        if (file.isDirectory()) {
//            return;
//        }
//        DataSource dataSource = fileHandlerService.getDataSourceForFileName(file);
//        if (dataSource == null) {
//            logger.error("There no mapping for for file hence ignoring the file processing");
//            ServiceUtil.moveFile(file, rootFolder, UNSUPPORTED);
//            return;
//        }
//
//        Map<String, String> primaryKeys = xmlService.getRelationKeysForDatasource(dataSource, null);
//        processDataFromFile(file, dataSource, primaryKeys);
//        ServiceUtil.moveFile(file, rootFolder, UNSUPPORTED);
//    }
//
//    private void processDataFromFile(File file, DataSource dataSource, Map<String, String> primaryKeys) {
//        String fileExtension = FilenameUtils.getExtension(file.getName());
//
//        if (StringUtils.isEmpty(fileExtension)) {
//            logger.error("Invalid file extension ignored the file " + file.getName() + "further processing!");
//            logger.error("Moving file to error folder");
//            ServiceUtil.moveFile(file, rootFolder, UNSUPPORTED);
//            return;
//        }
//        fileExtension = fileExtension.toLowerCase();
//        switch (fileExtension) {
//            case "xlsx":
//            case "xlsm":
//            case "xlsb":
//            case "xltx":
//            case "xltm":
//            case "xls":
//            case "xlt":
//                excelFileService.processExcelData(file, dataSource, primaryKeys);
//                break;
//            case "xml":
//                xmlFileService.processXmlData(file, dataSource, primaryKeys);
//                break;
//            case "pdf":
//                processPdf(file);
//                break;
//            case "json":
////TODO implement the file service
//                break;
//            case "csv":
//                csvFileService.processCsv(file, dataSource, primaryKeys);
//                break;
//            default:
//                ServiceUtil.moveFile(file, rootFolder, UNSUPPORTED);
//                break;
//        }
//    }

    private void processPdf(File file) {
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {

    }
}