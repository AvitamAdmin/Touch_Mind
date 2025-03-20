package com.touchMind.web.controllers.toolkit;

import com.touchMind.core.mongo.dto.CommonDto;
import com.touchMind.core.mongo.model.Node;
import com.touchMind.core.mongo.repository.NodeRepository;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.ModelService;
import com.touchMind.core.service.SiteService;
import com.touchMind.qa.utils.TestDataUtils;
import com.touchMind.tookit.service.ReportService;
import com.touchMind.web.controllers.AdminController;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Getter
@Setter
public class BaseController {

    public static final String P_NAMES = "pNames";
    public static final String P_VALUES = "pValues";
    public static final String ENDS_WITH_NUMBER = "^.*\\d$";
    public static final String TOOLKIT = "/toolkit/";
    public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String PRODUCT_DISCOUNT_VALUE = "product§discount§value";
    public static final String STOCK_REPORT = "stockReport";
    public static final String PRICE_REPORT = "priceReport";
    public static final String ADDON_REPORT = "addonReport";
    public static final String TRADEIN_REPORT = "tradeinReport";
    public static final String TOOLKIT_STOCK_REPORT = "toolkit/stockReport";
    public static final String TOOLKIT_PRICE_REPORT = "toolkit/priceReport";
    public static final String TOOLKIT_ADDON_REPORT = "toolkit/addonReport";
    public static final String TOOLKIT_TRADEIN_REPORT = "toolkit/tradeinReport";
    public static final String TEM_VARIANT = "temVariant";
    public static final String TEM_SITE = "temSite";
    private static final Map<String, String> URI_MAPPING = new HashMap<>() {{
        put(STOCK_REPORT, TOOLKIT_STOCK_REPORT);
        put(PRICE_REPORT, TOOLKIT_PRICE_REPORT);
        put(ADDON_REPORT, TOOLKIT_ADDON_REPORT);
        put(TRADEIN_REPORT, TOOLKIT_TRADEIN_REPORT);
    }};
    @Autowired
    protected NodeRepository nodeRepository;
    @Autowired
    protected CoreService coreService;
    Logger logger = LoggerFactory.getLogger(AdminController.class);
//    @Autowired
//    SubsidiaryRepository subsidiaryRepository;
    @Autowired
    ModelService modelService;
    @Autowired
    ReportService reportService;
    @Autowired
    private SiteService siteService;
    @Autowired
    private ObjectMapper objectMapper;

    protected String getUriForReportController(String key) {
        return URI_MAPPING.get(key);
    }

    //public Set<Subsidiary> getSubsidiariesForCurrentUser() {
//        return coreService.getCurrentUser().getSubsidiaries();
//    }

    protected ExampleMatcher getMatcher(CommonDto commonDto, boolean isEqual, String condition) {
        if (StringUtils.isNotEmpty(condition)) {
            if (condition.equalsIgnoreCase("or")) {
                if (isEqual) {
                    return ExampleMatcher.matchingAny().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
                } else {
                    return ExampleMatcher.matchingAny().withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
                }
            }
        }
        return ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
    }

    protected <T> T isSearchActive(T type) {
        if (type != null) {
            Field[] fields = type.getClass().getDeclaredFields();
            boolean isSearchActive = isSearchActive(fields, type);
            if (!isSearchActive) {
                Field[] superFields = type.getClass().getSuperclass().getDeclaredFields();
                isSearchActive = isSearchActive(superFields, type);
            }
            if (!isSearchActive) {
                Field[] superFields = type.getClass().getSuperclass().getSuperclass().getDeclaredFields();
                isSearchActive = isSearchActive(superFields, type);
            }
            return isSearchActive ? type : null;
        }
        return null;
    }

    private boolean isSearchActive(Field[] fields, Object type) {
        AtomicBoolean isSearchActive = new AtomicBoolean(false);
        Arrays.stream(fields).forEach(field -> {
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(type);
            } catch (Exception e) {
                logger.error("Exception getting field" + e);
            }
            if ((value != null && !(value instanceof Collection)) || (value != null && value instanceof Collection && CollectionUtils.isNotEmpty((Collection<?>) value))) {
                isSearchActive.set(true);
            }
        });
        return isSearchActive.get();
    }

    protected Pageable getPageable(int pageNumber, int pageSize, String sortDirection, String... sort) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();
        Arrays.stream(sort).collect(Collectors.toList()).forEach(field -> {
            Sort.Order order = new Sort.Order(direction, field);
            orders.add(order);
        });
        return sort != null ? PageRequest.of(pageNumber, pageSize, Sort.by(orders)) : PageRequest.of(pageNumber, pageSize);
    }

    protected void initialiseSessionForToolkitNodes(HttpServletRequest request, Map<String, String> requestMap) {
        Node parentNode = nodeRepository.findByPath("/toolkit");
        List<Node> lodeList = nodeRepository.findByParentNode(parentNode);
        lodeList.forEach(node -> {
            String currentUserSession = reportService.getCurrentUserSessionId(request, node.getIdentifier());
            requestMap.put(TestDataUtils.Field.TOOLKIT_SESSIONS.toString() + node.getId(), currentUserSession);
        });
    }

    protected void populateParameters(Map<String, String[]> parameterMap, Map<String, String> testMapData) {
        for (Map.Entry<String, String[]> entries : parameterMap.entrySet()) {
            testMapData.put(entries.getKey(), StringUtils.join(entries.getValue(), TestDataUtils.COMMA));
        }
    }
}
