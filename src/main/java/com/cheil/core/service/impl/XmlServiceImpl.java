package com.cheil.core.service.impl;

import com.cheil.core.HotFolderConstants;
import com.cheil.core.mongo.dto.ReportDto;
import com.cheil.core.mongo.repository.generic.GenericRepository;
import com.cheil.core.service.RepositoryService;
import com.cheil.core.service.XmlService;
import com.jayway.jsonpath.PathNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class XmlServiceImpl extends ApiCommonService implements XmlService, HotFolderConstants {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    Logger logger = LoggerFactory.getLogger(XmlServiceImpl.class);
    XPath xPath = null;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private XmlService xmlService;
    @Autowired
    private Environment env;

    public XmlServiceImpl() {
        xPath = XPathFactory.newInstance().newXPath();
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        XmlServiceImpl xmlService1 = new XmlServiceImpl();
        org.jsoup.nodes.Document doc = Jsoup.parse(new File("simplan.xml"));
        Elements elements = doc.getAllElements();
        ReportDto reportDto = new ReportDto();
        //xmlService1.processXml(doc,List.of("products_product availableServices code","products_product availableServices name","products_product_code"));
        Map<Object, Map<String, Object>> result = xmlService1.processXml(doc, reportDto, List.of("tariffCarriertList§carriers§tariffPlans §id"));
        System.out.println(result);
    }

    @Override
    public Map<Object, Map<String, Object>> processXml(org.jsoup.nodes.Document doc, ReportDto reportDto, List<String> inputParams) {
        Map<Object, Map<String, Object>> documents = new HashMap<>();
        for (String param : inputParams) {
            if (param.contains(" ")) {
                String[] parts = getParts(param);
                if (parts != null) {
                    String expression = parts[0].replaceAll("\\§", " > ");
                    String partTwo = parts[1].replaceAll("\\§", " > ");
                    Elements elements = doc.select(expression);
                    if (elements.size() > 0) {
                        processInDirectElementsForExpression(param, reportDto, elements, documents, partTwo);
                    }
                }
            } else {
                String expression = param.replaceAll("\\§", " > ");
                String attribute = null;
                if (param.contains("_") && !param.contains("contains") && !param.contains("g|")) {
                    String[] params = param.split("_");
                    expression = params[0].replaceAll("\\§", " > ");
                    attribute = params[1];
                }

                Elements elements = doc.select(expression);
                if (elements.size() > 0) {
                    if (param.contains("§promotionPrice§")) {
                        processDiscount(doc, reportDto, documents, elements);
                    }
                    processDirectElementsForExpression(param, reportDto, elements, documents, attribute);
                }
            }
        }
        if (documents.size() > 0) {
            processProductUrl(doc, reportDto, documents);
        }
        return documents;
    }

    private void processProductUrl(Document doc, ReportDto reportDto, Map<Object, Map<String, Object>> documents) {
        Map<String, Object> record = new HashMap<>();
        record.put("productUrl", reportDto.getSkuUrl());
        mergeDocument(documents, reportDto.getCurrentVariant() + "§" + reportDto.getCurrentSite(), record);
    }

    private void processDiscount(org.jsoup.nodes.Document doc, ReportDto reportDto, Map<Object, Map<String, Object>> documents, Elements promoPrice) {
        String expression = "product§price§value".replaceAll("\\§", " > ");
        Elements price = doc.select(expression);
        if (null != promoPrice && null != price) {
            df.setRoundingMode(RoundingMode.UP);
            double discount = Double.valueOf(promoPrice.text()) / Double.valueOf(price.text());
            double actualDiscount = 100 - (discount * 100);
            Map<String, Object> record = new HashMap<>();
            record.put("product§discount§value", df.format(actualDiscount) + "%");
            mergeDocument(documents, reportDto.getCurrentVariant() + "§" + reportDto.getCurrentSite(), record);
        }
    }

    private String getRelationValueForKey(Element element, String relationnKey) {
        String[] expression = getParts(relationnKey);
        try {
            String partTwo = expression[1].replaceAll("\\§", " > ");
            Elements elements = element.select(partTwo);
            return elements.text();
        } catch (PathNotFoundException e) {
            logger.error("No relation path found for key " + relationnKey);
        }
        return null;
    }

    private void processInDirectElementsForExpression(String param, ReportDto reportDto, Elements elements, Map<Object, Map<String, Object>> documents, String expression) {
        String recordId = reportDto.getCurrentVariant();
        String relationKey = reportDto.getRelationKeys();
        int counter = 0;
        String correctExpression = expression;
        if (param.endsWith("[*]")) {
            correctExpression = expression.substring(0, expression.lastIndexOf("[*]"));
        }
        for (int i = 0; i < elements.size(); i++) {
            Elements arrayElements = elements.get(i).select(correctExpression);
            Map<String, Object> newDoc = new HashMap<>();
            if (arrayElements.size() > 0 && param.endsWith("[*]")) {
                for (int j = 0; j < arrayElements.size(); j++) {
                    Element element = arrayElements.get(j);
                    String relationValue = getRelationValueForKey(arrayElements.get(i), relationKey);
                    if (element != null) {
                        newDoc.put(recordId + "§" + relationValue + "§" + param, element.text());
                        newDoc.put(recordId + "§" + relationValue + "§" + TEM_SITE, reportDto.getCurrentSite());
                    }
                }
                mergeDocument(documents, i, newDoc);
            } else {
                for (int j = 0; j < arrayElements.size(); j++) {
                    newDoc = new HashMap<>();
                    Element element = arrayElements.get(j);
                    String relationValue = getRelationValueForKey(arrayElements.get(j), relationKey);
                    if (element != null) {
                        newDoc.put(recordId + "§" + relationValue + "§" + i + "§" + j + "§" + param, element.text());
                        newDoc.put(recordId + "§" + relationValue + "§" + i + "§" + j + "§" + TEM_SITE, reportDto.getCurrentSite());
                        mergeDocument(documents, counter, newDoc);
                        counter++;
                    }
                }
            }
        }
    }

    private void processDirectElementsForExpression(String param, ReportDto reportDto, Elements elements, Map<Object, Map<String, Object>> documents, String attribute) {
        Map<String, Object> record = new HashMap<>();
        String originalAttr = attribute;
        for (Integer i = 0; i < elements.size(); i++) {
            String filterAttr = null;
            String filterVal = null;
            if (StringUtils.isNotEmpty(originalAttr) && originalAttr.contains("-filter")) {
                String[] attributes = originalAttr.split("-filter");
                attribute = attributes[0];
                filterAttr = attributes[1];
            }
            if (StringUtils.isNotEmpty(filterAttr)) {
                String[] filterArr = filterAttr.split(":");
                filterAttr = filterArr[0].replaceAll("\\(", "");
                filterVal = filterArr[1].replaceAll("\\)", "");
            }
            Map<String, Object> fields = getRecordForElement(param, elements.get(i), attribute);
            Map<String, Object> filterFields = new HashMap<>();
            if (StringUtils.isNotEmpty(filterAttr)) {
                filterFields = getRecordForElement(param, elements.get(i), filterAttr);
                if (!filterFields.get(param).equals(filterVal)) {
                    filterFields = new HashMap<>();
                }
            }
            if ((StringUtils.isNotEmpty(filterAttr) && StringUtils.isNotEmpty(filterVal) && !filterFields.isEmpty()) || StringUtils.isEmpty(filterAttr)) {
                for (String field : fields.keySet()) {
                    String value = record.containsKey(field) ? record.get(field) + "," + fields.get(field) : (String) fields.get(field);
                    if (field.contains("§CategoryCodePath") && value.contains("|")) {
                        String[] values = value.split("\\|");
                        value = values[values.length - 1];
                    }
                    record.put(field, value);
                }
                mergeDocument(documents, reportDto.getCurrentVariant() + "§" + reportDto.getCurrentSite(), record);
            }
        }
    }

    private Map<String, Object> getRecordForElement(String param, Element element, String attribute) {
        Map<String, Object> record = new HashMap<>();
        if (element != null) {
            Optional<Element> elmOpt = element.getAllElements().stream().findFirst();
            if (elmOpt.isPresent()) {
                if (null != attribute) {
                    String value = element.attr(attribute);
                    record.put(param, value);
                } else {
                    record.put(param, elmOpt.get().text());
                }
            }
        }
        return record;
    }

    @Override
    public boolean processXmlData(org.jsoup.nodes.Document doc, ReportDto reportDto) {
        if (StringUtils.isEmpty(reportDto.getRelationKeys())) {
            logger.error("No primary key found for entity hence ignoring the further processing");
            return false;
        }
        Map<Object, Map<String, Object>> documents = xmlService.processXml(doc, reportDto, reportDto.getDataSourceParams());
        GenericRepository genericRepository = repositoryService.getRepositoryForNodeId(reportDto.getCurrentNode());
        return saveDocuments(documents, genericRepository, reportDto);
    }
}
