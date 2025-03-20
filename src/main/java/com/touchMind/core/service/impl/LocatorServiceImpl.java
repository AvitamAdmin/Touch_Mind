package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.TestLocatorGroupDto;
import com.touchMind.core.mongo.model.LocatorPriority;
import com.touchMind.core.mongo.model.Site;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongo.model.TestLocatorGroup;
import com.touchMind.core.mongo.repository.TestLocatorGroupRepository;
import com.touchMind.core.mongo.repository.TestLocatorRepository;
import com.touchMind.core.service.LocatorGroupService;
import com.touchMind.core.service.LocatorService;
import com.touchMind.core.service.SiteService;
import com.touchMind.form.LocatorForm;
import com.touchMind.form.LocatorSelectorDto;
import com.touchMind.qa.strategies.ActionType;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

@Service
public class LocatorServiceImpl implements LocatorService {

    @Autowired
    private TestLocatorRepository testLocatorRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SiteService siteService;

    @Autowired
    private LocatorGroupService locatorGroupService;

    @Autowired
    private TestLocatorGroupRepository testLocatorGroupRepository;

    private static void populateExistingData(LocatorForm locator, TestLocator testLocator, TestLocator testLocatorRecord) {
        testLocator.setId(testLocatorRecord.getId());
        //testLocator.setTestDataType(testLocatorRecord.getTestDataType());
        //testLocator.setTestDataSubtype(testLocatorRecord.getTestDataSubtype());
        if (null == locator.getUiLocatorSelector()) {
            testLocator.setUiLocatorSelector(testLocatorRecord.getUiLocatorSelector());
        }
    }

    @Override
    public List<TestLocatorGroupDto> getLocatorGroups(String recordId) {
        List<TestLocatorGroupDto> locatorGroupsList = new ArrayList<>();
        List<TestLocatorGroup> locatorGroups = locatorGroupService.getLocatorsGroup();
        locatorGroups.stream().forEach(locatorGroup -> {
            List<LocatorPriority> locatorPriorities = locatorGroup.getTestLocators();
            if (null != locatorPriorities) {
                locatorPriorities.stream().forEach(locatorPriority -> {
                    if (StringUtils.isNotEmpty(locatorPriority.getLocatorId()) && locatorPriority.getLocatorId().equals(recordId)) {
                        TestLocatorGroupDto testLocatorGroupDto = modelMapper.map(locatorGroup, TestLocatorGroupDto.class);
                        testLocatorGroupDto.setErrorMsg(locatorPriority.getErrorMsg());
                        locatorGroupsList.add(testLocatorGroupDto);
                    }
                });
            }
        });
        return locatorGroupsList;
    }

    @Override
    public List<TestLocator> getLocators() {
        return testLocatorRepository.findAllByOrderByIdentifier();
    }

    @Override
    public LocatorForm editLocator(String locatorId) {
        TestLocator testLocator = testLocatorRepository.findByIdentifier(locatorId);
        if (testLocator != null) {
            LocatorForm locatorForm = modelMapper.map(testLocator, LocatorForm.class);
            updateFormWithSiteMap(locatorForm);
            return locatorForm;
        }
        return null;
    }

    @Override
    public void deleteLocator(String locatorId) {
        testLocatorRepository.deleteByIdentifier(locatorId);
    }

    @Override
    public TestLocator getLocatorById(String locatorId) {
        return testLocatorRepository.findByIdentifier(locatorId);
    }

    @Override
    public List<String> getMethodNames() {
        List<String> methodNames = new ArrayList<>();
        Class actionTypeClass = ActionType.class;
        Field[] fields = actionTypeClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                methodNames.add(String.valueOf(fields[i].get(fields[i])));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return methodNames;
    }

    @Override
    public void getFormWithSiteMap(LocatorForm locatorForm) {
        List<Site> sites = siteService.findBySubsidiaryAndStatusOrderBySiteId(true);
        SortedMap<String, LocatorSelectorDto> locatorSelectorFormSortedMap = new TreeMap<>();
        locatorSelectorFormSortedMap.put("default", new LocatorSelectorDto());
        sites.stream().forEach(site -> {
            locatorSelectorFormSortedMap.put(site.getIdentifier(), new LocatorSelectorDto());
        });
        locatorForm.setUiLocatorSelector(locatorSelectorFormSortedMap);
    }

    public void updateFormWithSiteMap(LocatorForm locatorForm) {
        List<Site> sites = siteService.findBySubsidiaryAndStatusOrderBySiteId(true);
        SortedMap<String, LocatorSelectorDto> siteMapForUiSelector = locatorForm.getUiLocatorSelector();
        if (siteMapForUiSelector == null) {
            siteMapForUiSelector = new TreeMap<>();
        }
        SortedMap<String, LocatorSelectorDto> finalSiteMapForUiSelector = siteMapForUiSelector;
        sites.stream().forEach(site -> {
            LocatorSelectorDto locatorSelectorDto = new LocatorSelectorDto();
            String siteId = site.getIdentifier();
            if (!finalSiteMapForUiSelector.containsKey(siteId)) {
                finalSiteMapForUiSelector.put(siteId, locatorSelectorDto);
            }
        });
        locatorForm.setUiLocatorSelector(siteMapForUiSelector);
        //locatorForm.setTestLocatorGroupList(getLocatorGroups());
    }

    @Override
    public void saveLocator(LocatorForm locatorForm) {
        TestLocator testLocator = testLocatorRepository.findByIdentifier(locatorForm.getIdentifier());
        testLocator.setUiLocatorSelector(locatorForm.getUiLocatorSelector());
        testLocatorRepository.save(testLocator);
    }
}
