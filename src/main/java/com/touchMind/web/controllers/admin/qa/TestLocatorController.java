package com.touchMind.web.controllers.admin.qa;

import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.dto.TestLocatorDto;
import com.touchMind.core.mongo.dto.TestLocatorWsDto;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.SiteRepository;
import com.touchMind.core.mongo.repository.TestLocatorRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.LocatorService;
import com.touchMind.core.service.TestLocatorService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
import com.touchMind.qa.service.impl.SelectorServiceImpl;
import com.touchMind.web.controllers.BaseController;
import com.google.common.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/admin/locator")
public class TestLocatorController extends BaseController {

    public static final String ADMIN_LOCATOR = "/admin/locator";
    Logger logger = LoggerFactory.getLogger(TestLocatorController.class);

    @Autowired
    private LocatorService locatorService;

    @Autowired
    private TestLocatorRepository testLocatorRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestLocatorService testLocatorService;
    @Autowired
    private BaseService baseService;

    @PostMapping
    @ResponseBody
    public TestLocatorWsDto getTestLocators(@RequestBody TestLocatorWsDto testLocatorWsDto) {
        Pageable pageable = getPageable(testLocatorWsDto.getPage(), testLocatorWsDto.getSizePerPage(), testLocatorWsDto.getSortDirection(), testLocatorWsDto.getSortField());
        TestLocatorDto testLocatorDto = CollectionUtils.isNotEmpty(testLocatorWsDto.getTestLocators()) ? testLocatorWsDto.getTestLocators().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(testLocatorDto, testLocatorWsDto.getOperator());
        TestLocator testLocator = testLocatorDto != null ? modelMapper.map(testLocatorDto, TestLocator.class) : null;
        Page<TestLocator> page = isSearchActive(testLocator) != null ? testLocatorRepository.findAll(Example.of(testLocator, exampleMatcher), pageable) : testLocatorRepository.findAll(pageable);
        Type listType = new TypeToken<List<TestLocatorDto>>() {
        }.getType();
        testLocatorWsDto.setTestLocators(modelMapper.map(page.getContent(), listType));
        testLocatorWsDto.setBaseUrl(ADMIN_LOCATOR);
        testLocatorWsDto.setTotalPages(page.getTotalPages());
        testLocatorWsDto.setTotalRecords(page.getTotalElements());
        testLocatorWsDto.setAttributeList(getConfiguredAttributes(testLocatorWsDto.getNode()));
        testLocatorWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.TEST_LOCATOR));
        return testLocatorWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new TestLocator());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.TEST_LOCATOR);
    }

    @GetMapping("/get")
    public TestLocatorWsDto getTestLocators() {
        TestLocatorWsDto testLocatorWsDto = new TestLocatorWsDto();
        Type listType = new TypeToken<List<TestLocatorDto>>() {
        }.getType();
        testLocatorWsDto.setTestLocators(modelMapper.map(testLocatorRepository.findByStatusOrderByIdentifier(true), listType));
        testLocatorWsDto.setBaseUrl(ADMIN_LOCATOR);
        return testLocatorWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody TestLocatorDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(testLocatorRepository.findByIdentifier(recordId), TestLocatorDto.class);
    }

    @GetMapping("/add")
    @ResponseBody
    public TestLocatorWsDto getLocatorForm() {
        TestLocatorWsDto testLocatorWsDto = new TestLocatorWsDto();
        testLocatorWsDto.setBaseUrl(ADMIN_LOCATOR);
        testLocatorWsDto.setSELECTOR_STRATEGIES(SelectorServiceImpl.SELECTOR_STRATEGIES);
        testLocatorWsDto.setMethods(locatorService.getMethodNames());
        testLocatorWsDto.setBaseUrl(ADMIN_LOCATOR);
        return testLocatorWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public TestLocatorWsDto addLocator(@RequestBody TestLocatorWsDto request) {
        return testLocatorService.handleEdit(request);
    }

    @PostMapping("/delete")
    @ResponseBody
    public TestLocatorWsDto deleteLocator(@RequestBody TestLocatorWsDto testLocatorWsDto) {
        for (TestLocatorDto testLocatorDto : testLocatorWsDto.getTestLocators()) {
            testLocatorRepository.deleteByIdentifier(testLocatorDto.getIdentifier());
        }
        testLocatorWsDto.setMessage("Data deleted successfully!!");
        testLocatorWsDto.setBaseUrl(ADMIN_LOCATOR);
        return testLocatorWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public TestLocatorWsDto editMultiple(@RequestBody TestLocatorWsDto request) {
        TestLocatorWsDto testLocatorWsDto = new TestLocatorWsDto();
        List<TestLocatorDto> testLocators = new ArrayList<>();
        for (TestLocatorDto testLocatorDto : request.getTestLocators()) {
            testLocatorDto.setTestLocatorGroups(locatorService.getLocatorGroups(testLocatorDto.getIdentifier()));
            modelMapper.map(testLocatorRepository.findByIdentifier(testLocatorDto.getIdentifier()), testLocatorDto);
            testLocators.add(testLocatorDto);
        }
        testLocatorWsDto.setTestLocators(testLocators);
        testLocatorWsDto.setSELECTOR_STRATEGIES(SelectorServiceImpl.SELECTOR_STRATEGIES);
        testLocatorWsDto.setMethods(locatorService.getMethodNames());
        testLocatorWsDto.setRedirectUrl("/admin/locator");
        testLocatorWsDto.setBaseUrl(ADMIN_LOCATOR);
        return testLocatorWsDto;
    }

    @RequestMapping(value = "/copy", method = RequestMethod.GET)
    public @ResponseBody TestLocatorWsDto copyLocator(@RequestParam("recordId") String recordId) {
        TestLocatorWsDto testLocatorWsDto = new TestLocatorWsDto();
        TestLocator testLocator = testLocatorRepository.findByIdentifier(recordId);
        TestLocatorDto testLocatorDto = modelMapper.map(testLocator, TestLocatorDto.class);
        testLocatorDto.setIdentifier(null);
        testLocatorDto.setCreationTime(new Date());
        testLocatorDto.setLastModified(new Date());
        testLocatorDto.setIdentifier("Copy_" + testLocator.getIdentifier());
        TestLocator clonedLocator = modelMapper.map(testLocatorDto, TestLocator.class);
        testLocatorRepository.save(clonedLocator);
        String id = String.valueOf(clonedLocator.getId().getTimestamp());
        if (testLocatorRepository.findByIdentifier(id) != null) {
            id = id + new Random().nextInt(24565);
        }
        clonedLocator.setIdentifier(id);
        clonedLocator.setIdentifier("Copy_" + id + testLocator.getIdentifier());
        testLocatorRepository.save(clonedLocator);
        testLocatorWsDto.setMessage("Locator cloned successfully!!");
        return testLocatorWsDto;
    }

    @PostMapping("/upload")
    @ResponseBody
    public TestLocatorWsDto uploadFile(@RequestBody MultipartFile file) {
        TestLocatorWsDto testLocatorWsDto = new TestLocatorWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.TEST_LOCATOR, EntityConstants.TEST_LOCATOR, testLocatorWsDto);
            if (StringUtils.isEmpty(testLocatorWsDto.getMessage())) {
                testLocatorWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return testLocatorWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public TestLocatorWsDto uploadFile(@RequestBody TestLocatorWsDto testLocatorWsDto) {

        try {
            testLocatorWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.TEST_LOCATOR, testLocatorWsDto.getHeaderFields()));
            return testLocatorWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}

