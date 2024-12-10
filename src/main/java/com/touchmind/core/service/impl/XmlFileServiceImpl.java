package com.touchmind.core.service.impl;

import com.touchmind.core.HotFolderConstants;
import com.touchmind.core.mongo.model.DataSource;
import com.touchmind.core.mongo.repository.generic.GenericRepository;
import com.touchmind.core.service.RepositoryService;
import com.touchmind.core.service.ServiceUtil;
import com.touchmind.core.service.XmlFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class XmlFileServiceImpl implements XmlFileService, HotFolderConstants {

    private static String rootFolder = null;
    Logger logger = LoggerFactory.getLogger(XmlServiceImpl.class);
    XPath xPath = null;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private Environment env;

    public XmlFileServiceImpl() {
        xPath = XPathFactory.newInstance().newXPath();
    }

    @Override
    public boolean processXmlData(File file, DataSource dataSource, Map<String, String> primaryKeys) {

        if (rootFolder == null) {
            rootFolder = ServiceUtil.getProcessedFolderLocation(env);
        }

        Collection<String> primaryKey = primaryKeys.values();
        Optional<String> optionalPk = primaryKey.stream().findFirst();

        if (!optionalPk.isPresent()) {
            logger.error("No primary key found for entity hence ignoring the file for further processing : " + file.getName());
            ServiceUtil.moveFile(file, rootFolder, ERROR);
            return false;
        }


        Optional<String> optionalEntityId = primaryKeys.keySet().stream().findFirst();
        if (!optionalEntityId.isPresent()) {
            logger.error("No mapping found for entity hence ignoring the file for further processing : " + file.getName());
            ServiceUtil.moveFile(file, rootFolder, UNSUPPORTED);
            return false;
        }

        String entityId = optionalEntityId.get();
        GenericRepository genericRepository = repositoryService.getRepositoryForRelationId(entityId);

        if (genericRepository == null) {
            logger.error("Unknown repository ignoring file for further processing" + file.getName());
            ServiceUtil.moveFile(file, rootFolder, ERROR);
            return false;
        }

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
            Document xmlDocument = docBuilder.parse(file);
            List<String> inputParams = dataSource.getSrcInputParams();
            //TODO Save entities
            // return  xmlService.processXml(xmlDocument,null);
            return true;
        } catch (Exception e) {
            logger.error("Invalid xml file " + file.getName() + " processing this file ignored !" + e);
            ServiceUtil.moveFile(file, rootFolder, ERROR);
        }
        return false;
    }
}
