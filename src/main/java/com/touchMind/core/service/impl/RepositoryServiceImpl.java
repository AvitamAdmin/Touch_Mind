package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.model.AddonDocuments;
import com.touchMind.core.mongo.model.AvailabilityDocuments;
import com.touchMind.core.mongo.model.BaseDocuments;
import com.touchMind.core.mongo.model.BenefitsDocuments;
import com.touchMind.core.mongo.model.EupDocuments;
import com.touchMind.core.mongo.model.EwarrantyDocuments;
import com.touchMind.core.mongo.model.GiftDocuments;
import com.touchMind.core.mongo.model.Node;
import com.touchMind.core.mongo.model.PriceDocuments;
import com.touchMind.core.mongo.model.ScPlusDocuments;
import com.touchMind.core.mongo.model.SimDocuments;
import com.touchMind.core.mongo.model.StockDocuments;
import com.touchMind.core.mongo.model.TradeInDocuments;
import com.touchMind.core.mongo.model.baseEntity.BaseEntity;
import com.touchMind.core.mongo.repository.AddonDocumentsRepository;
import com.touchMind.core.mongo.repository.AvailabilityDocumentsRepository;
import com.touchMind.core.mongo.repository.BaseDocumentsRepository;
import com.touchMind.core.mongo.repository.BenefitsDocumentsRepository;
import com.touchMind.core.mongo.repository.EupDocumentsRepository;
import com.touchMind.core.mongo.repository.EwarrantyDocumentsRepository;
import com.touchMind.core.mongo.repository.GiftDocumentsRepository;
import com.touchMind.core.mongo.repository.NodeRepository;
import com.touchMind.core.mongo.repository.PriceDocumentsRepository;
import com.touchMind.core.mongo.repository.ScPlusDocumentsRepository;
import com.touchMind.core.mongo.repository.SmDocumentsRepository;
import com.touchMind.core.mongo.repository.StockDocumentsRepository;
import com.touchMind.core.mongo.repository.TradeInDocumentsRepository;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import com.touchMind.core.mongo.repository.generic.GenericRepository;
import com.touchMind.core.service.RepositoryService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RepositoryServiceImpl implements RepositoryService {

    public static final String STOCK_DOCUMENTS = "StockDocuments";
    public static final String PRICE_DOCUMENTS = "PriceDocuments";
    public static final String ADDON_DOCUMENTS = "AddonDocuments";
    public static final String SM_DOCUMENTS = "SmDocuments";
    public static final String TRADE_IN_DOCUMENTS = "TradeInDocuments";
    public static final String SC_DOCUMENTS = "ScDocuments";
    public static final String EUP_DOCUMENTS = "EupDocuments";
    public static final String EWARRANTY_DOCUMENTS = "EwarrantyDocuments";
    public static final String BENEFITS_DOCUMENTS = "BenefitsDocuments";
    public static final String AVAILABILITY_DOCUMENTS = "AvailabilityDocuments";
    public static final String GIFT_DOCUMENTS = "GiftDocuments";
    public static final String BASE_DOCUMENTS = "BaseDocuments";
    Logger LOG = LoggerFactory.getLogger(RepositoryServiceImpl.class);
    //public static final String NODE_DOCUMENTS = "Node";
    Map<String, GenericRepository> repositoryMap;
    @Autowired
    private StockDocumentsRepository stockDocumentsRepository;
    @Autowired
    private PriceDocumentsRepository priceDocumentsRepository;
    @Autowired
    private AddonDocumentsRepository addonDocumentsRepository;
    @Autowired
    private EupDocumentsRepository eupDocumentsRepository;
    @Autowired
    private ScPlusDocumentsRepository scDocumentsRepository;
    @Autowired
    private SmDocumentsRepository smDocumentsRepository;
    @Autowired
    private TradeInDocumentsRepository tradeInDocumentsRepository;
    @Autowired
    private EwarrantyDocumentsRepository ewarrantyDocumentsRepository;
    @Autowired
    private BenefitsDocumentsRepository benefitsDocumentsRepository;
    @Autowired
    private AvailabilityDocumentsRepository availabilityDocumentsRepository;
    @Autowired
    private BaseDocumentsRepository baseDocumentsRepository;
    @Autowired
    private GiftDocumentsRepository giftDocumentsRepository;
    @Autowired
    private NodeRepository nodeRepository;
    //Map<String, GenericImportRepository> importRepositoryMap;
    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void initRepositories() {
        repositoryMap = new HashMap<>();
        //importRepositoryMap = new HashMap<>();
        repositoryMap.put(STOCK_DOCUMENTS, stockDocumentsRepository);
        repositoryMap.put(PRICE_DOCUMENTS, priceDocumentsRepository);
        repositoryMap.put(ADDON_DOCUMENTS, addonDocumentsRepository);
        repositoryMap.put(SM_DOCUMENTS, smDocumentsRepository);
        repositoryMap.put(TRADE_IN_DOCUMENTS, tradeInDocumentsRepository);
        repositoryMap.put(SC_DOCUMENTS, scDocumentsRepository);
        repositoryMap.put(EUP_DOCUMENTS, eupDocumentsRepository);
        repositoryMap.put(EWARRANTY_DOCUMENTS, ewarrantyDocumentsRepository);
        repositoryMap.put(BENEFITS_DOCUMENTS, benefitsDocumentsRepository);
        repositoryMap.put(AVAILABILITY_DOCUMENTS, availabilityDocumentsRepository);
        repositoryMap.put(BASE_DOCUMENTS, baseDocumentsRepository);
        repositoryMap.put(GIFT_DOCUMENTS, giftDocumentsRepository);

        //importRepositoryMap.put(NODE_DOCUMENTS,nodeRepository);
    }

    @Override
    public GenericImportRepository getRepositoryForName(String entityName) {
        try {
            return (GenericImportRepository) applicationContext.getBean(entityName + "Repository");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public GenericRepository getRepositoryForRelationId(String relationId) {
        return repositoryMap.get(relationId);
    }

    @Override
    public GenericRepository getRepositoryForNodeId(String nodeId) {
        if (nodeId == null) {
            return repositoryMap.get(BASE_DOCUMENTS);
        }
        //TODO check if this fetch the record correctly
        Node node = nodeRepository.findByIdentifier(nodeId);
        String path = null;
        if (node != null) {
            path = node.getPath();
        } else {
            return repositoryMap.get(BASE_DOCUMENTS);
        }
        switch (path) {
            case "/toolkit/stockReport":
                return repositoryMap.get(STOCK_DOCUMENTS);
            case "/toolkit/priceReport":
                return repositoryMap.get(PRICE_DOCUMENTS);
            case "/toolkit/addonReport":
                return repositoryMap.get(ADDON_DOCUMENTS);
            case "/toolkit/smReport":
                return repositoryMap.get(SM_DOCUMENTS);
            case "/toolkit/tradeinReport":
                return repositoryMap.get(TRADE_IN_DOCUMENTS);
            case "/toolkit/scplusReport":
                return repositoryMap.get(SC_DOCUMENTS);
            case "/toolkit/eupReport":
                return repositoryMap.get(EUP_DOCUMENTS);
            case "/toolkit/giftReport":
                return repositoryMap.get(GIFT_DOCUMENTS);
            case "/toolkit/ewarranty":
                return repositoryMap.get(EWARRANTY_DOCUMENTS);
            case "/toolkit/benefits":
                return repositoryMap.get(BENEFITS_DOCUMENTS);
            case "/toolkit/availability":
                return repositoryMap.get(AVAILABILITY_DOCUMENTS);
            default:
                return repositoryMap.get(BASE_DOCUMENTS);
        }
    }

    @Override
    public BaseEntity getEntityForNodeId(String nodeId) {
        if (nodeId == null) {
            return getNewEntityForName(BASE_DOCUMENTS);
        }
        //TODO check if this fetch the record correctly
        Node node = nodeRepository.findByIdentifier(nodeId);
        String path = null;
        if (node != null) {
            path = node.getPath();
        } else {
            return new BaseDocuments();
        }
        switch (path) {
            case "/toolkit/stockReport":
                return getNewEntityForName(STOCK_DOCUMENTS);
            case "/toolkit/priceReport":
                return getNewEntityForName(PRICE_DOCUMENTS);
            case "/toolkit/addonReport":
                return getNewEntityForName(ADDON_DOCUMENTS);
            case "/toolkit/smReport":
                return getNewEntityForName(SM_DOCUMENTS);
            case "/toolkit/tradeinReport":
                return getNewEntityForName(TRADE_IN_DOCUMENTS);
            case "/toolkit/scplusReport":
                return getNewEntityForName(SC_DOCUMENTS);
            case "/toolkit/eupReport":
                return getNewEntityForName(EUP_DOCUMENTS);
            case "/toolkit/giftReport":
                return getNewEntityForName(GIFT_DOCUMENTS);
            case "/toolkit/ewarranty":
                return getNewEntityForName(EWARRANTY_DOCUMENTS);
            case "/toolkit/benefits":
                return getNewEntityForName(BENEFITS_DOCUMENTS);
            case "/toolkit/availability":
                return getNewEntityForName(AVAILABILITY_DOCUMENTS);
            default:
                return getNewEntityForName(BASE_DOCUMENTS);
        }
    }

    @Override
    public BaseEntity getNewEntityForName(String name) {
        switch (name) {
            case STOCK_DOCUMENTS:
                return new StockDocuments();
            case PRICE_DOCUMENTS:
                return new PriceDocuments();
            case ADDON_DOCUMENTS:
                return new AddonDocuments();
            case SM_DOCUMENTS:
                return new SimDocuments();
            case TRADE_IN_DOCUMENTS:
                return new TradeInDocuments();
            case SC_DOCUMENTS:
                return new ScPlusDocuments();
            case EUP_DOCUMENTS:
                return new EupDocuments();
            case EWARRANTY_DOCUMENTS:
                return new EwarrantyDocuments();
            case BENEFITS_DOCUMENTS:
                return new BenefitsDocuments();
            case AVAILABILITY_DOCUMENTS:
                return new AvailabilityDocuments();
            case GIFT_DOCUMENTS:
                return new GiftDocuments();
            default:
                return new BaseDocuments();
        }
    }
}
