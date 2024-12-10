package com.touchmind.web.controllers;

import com.touchmind.core.mongo.dto.CommonDto;
import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.model.CommonBasicFields;
import com.touchmind.core.mongo.model.InterfaceConfig;
import com.touchmind.core.mongo.repository.InterfaceConfigRepository;
import com.touchmind.core.service.CommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class BaseController {

    @Autowired
    private CommonService commonService;

    @Autowired
    private InterfaceConfigRepository interfaceConfigRepository;

    Logger logger = LoggerFactory.getLogger(BaseController.class);

    protected List<SearchDto> getConfiguredAttributes(String node) {
        InterfaceConfig interfaceConfig = interfaceConfigRepository.findByNode(node);
        return interfaceConfig != null ? interfaceConfig.getAttributes() : Collections.emptyList();
    }

    protected ExampleMatcher getMatcher(CommonDto commonDto, String condition) {
        ExampleMatcher matcher = null;
        if (commonDto != null) {
            Map<String, String> commonDtoMap = commonService.convertToMap(commonDto);
            if (MapUtils.isNotEmpty(commonDtoMap)) {
                if (StringUtils.isNotEmpty(condition)) {
                    if (condition.equalsIgnoreCase("or")) {
                        matcher = ExampleMatcher.matchingAny().withIgnoreNullValues().withIgnoreCase();
                    } else {
                        matcher = ExampleMatcher.matching().withIgnoreNullValues().withIgnoreCase();
                    }
                    for (Map.Entry<String, String> entry : commonDtoMap.entrySet()) {
                        String value = entry.getValue();
                        if (StringUtils.isNotEmpty(value) && value.contains("|")) {
                            String[] values = value.split("\\|");
                            String operator = values[1];
                            if (operator.equalsIgnoreCase("equals")) {
                                matcher.withMatcher(entry.getKey(), new ExampleMatcher.GenericPropertyMatcher().exact());
                            } else {
                                matcher.withMatcher(entry.getKey(), new ExampleMatcher.GenericPropertyMatcher().contains());
                            }
                        }
                    }
                    return matcher;
                }
            }
        }
        return ExampleMatcher.matching().withIgnoreNullValues().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING).withIgnoreCase();
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

    protected List<SearchDto> getGroupedParentAndChildAttributes(CommonBasicFields type) {
        List<SearchDto> searchDtoList = new ArrayList<>();
        String regex = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])";
        Field[] fields = type.getClass().getDeclaredFields();
        Field[] staticFields = type.getClass().getSuperclass().getDeclaredFields();
        Field[] basicStaticFields = type.getClass().getSuperclass().getSuperclass().getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            SearchDto searchDto = new SearchDto();
            String attrName = field.getName();
            String[] labels = attrName.split(regex);
            searchDto.setAttribute(attrName);
            String label = labels.length > 1 ? labels[0] + " " + labels[1] : labels[0];
            searchDto.setLabel(StringUtils.capitalize(label));
            searchDto.setDynamicAttr(true);
            searchDtoList.add(searchDto);
        });
        Arrays.stream(staticFields).forEach(field -> {
            SearchDto searchDto = new SearchDto();
            String attrName = field.getName();
            String[] labels = attrName.split(regex);
            searchDto.setAttribute(attrName);
            String label = labels.length > 1 ? labels[0] + " " + labels[1] : labels[0];
            searchDto.setLabel(StringUtils.capitalize(label));
            searchDto.setDynamicAttr(false);
            searchDtoList.add(searchDto);
        });
        Arrays.stream(basicStaticFields).forEach(field -> {
            SearchDto searchDto = new SearchDto();
            String attrName = field.getName();
            String[] labels = attrName.split(regex);
            searchDto.setAttribute(attrName);
            String label = labels.length > 1 ? labels[0] + " " + labels[1] : labels[0];
            searchDto.setLabel(StringUtils.capitalize(label));
            searchDto.setDynamicAttr(false);
            searchDtoList.add(searchDto);
        });
        return searchDtoList;
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

    protected Pageable getPageable(int pageNumber, int pageSize, Sort.Direction direction, String... sort) {
        List<Sort.Order> orders = new ArrayList<>();
        Arrays.stream(sort).collect(Collectors.toList()).forEach(field -> {
            Sort.Order order = new Sort.Order(direction, field);
            orders.add(order);
        });
        return sort != null ? PageRequest.of(pageNumber, pageSize, Sort.by(orders)) : PageRequest.of(pageNumber, pageSize);
    }
}