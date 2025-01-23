package com.touchmind.web.controllers.admin.qa;

import com.touchmind.core.mongo.dto.SavedQueryDto;
import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.dto.TestLocatorDto;
import com.touchmind.core.mongo.dto.TestLocatorWsDto;
import com.touchmind.core.mongo.model.Site;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.SiteRepository;
import com.touchmind.core.mongo.repository.TestLocatorRepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.LocatorService;
import com.touchmind.core.service.TestLocatorService;
import com.touchmind.fileimport.service.FileExportService;
import com.touchmind.fileimport.service.FileImportService;
import com.touchmind.fileimport.strategies.EntityType;
import com.touchmind.form.LocatorSelectorDto;
import com.touchmind.qa.service.impl.SelectorServiceImpl;
import com.touchmind.web.controllers.BaseController;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

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
        testLocatorWsDto.setTestLocators(modelMapper.map(page.getContent(), List.class));
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
        testLocatorWsDto.setTestLocators(modelMapper.map(testLocatorRepository.findByStatusOrderByIdentifier(true), List.class));
        testLocatorWsDto.setBaseUrl(ADMIN_LOCATOR);
        return testLocatorWsDto;
    }

    @GetMapping("/migrate")
    @ResponseBody
    public void migrateData() {
        List<TestLocator> testLocators = testLocatorRepository.findAll();
        for (TestLocator locator : testLocators) {
            List<Site> sites = siteRepository.findByStatusOrderByIdentifier(true);
            SortedMap<String, LocatorSelectorDto> locatorSelectorFormMap = new TreeMap<>();
            for (Site site : sites) {
                LocatorSelectorDto locatorSelectorDto = new LocatorSelectorDto();
                //locatorSelectorDto.setXpathSelector(locator.getUiSelector(site.getIdentifier()));
                //locatorSelectorDto.setInputData(locator.getInputData(site.getIdentifier()));
                locatorSelectorFormMap.put(site.getIdentifier(), locatorSelectorDto);
                locator.setUiLocatorSelector(locatorSelectorFormMap);
            }
            testLocatorRepository.save(locator);
        }
    }

    @RequestMapping(value = "/getByRecordId", method = RequestMethod.GET)
    public @ResponseBody TestLocatorDto getByRecordId(@RequestParam("recordId") String recordId) {
        return modelMapper.map(testLocatorRepository.findByRecordId(recordId), TestLocatorDto.class);
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
            testLocatorRepository.deleteByRecordId(testLocatorDto.getRecordId());
        }
        testLocatorWsDto.setMessage("Data deleted successfully!!");
        testLocatorWsDto.setBaseUrl(ADMIN_LOCATOR);
        return testLocatorWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public TestLocatorWsDto editMultiple(@RequestBody TestLocatorWsDto request) {
        TestLocatorWsDto testLocatorWsDto = new TestLocatorWsDto();
        List<TestLocator> testLocators = new ArrayList<>();
        for (TestLocatorDto testLocatorDto : request.getTestLocators()) {
            testLocators.add(testLocatorRepository.findByRecordId(testLocatorDto.getRecordId()));
        }
        testLocatorWsDto.setTestLocators(modelMapper.map(testLocators, List.class));
        testLocatorWsDto.setSELECTOR_STRATEGIES(SelectorServiceImpl.SELECTOR_STRATEGIES);
        testLocatorWsDto.setMethods(locatorService.getMethodNames());
        testLocatorWsDto.setRedirectUrl("/admin/locator");
        testLocatorWsDto.setBaseUrl(ADMIN_LOCATOR);
        return testLocatorWsDto;
    }

    @RequestMapping(value = "/copyForm/{id}", method = RequestMethod.GET)
    public @ResponseBody String copyLocator(@PathVariable("id") String id) {
        TestLocatorDto testLocatorDto = locatorService.editLocator(id);
        testLocatorDto.setIdentifier(testLocatorDto.getIdentifier() + "_" + ObjectId.get() + "_COPY");
        testLocatorDto.setRecordId(null);
        locatorService.addLocator(testLocatorDto);
        return "success";
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

    @GetMapping("/export")
    @ResponseBody
    public TestLocatorWsDto uploadFile() {
        TestLocatorWsDto testLocatorWsDto = new TestLocatorWsDto();
        try {
            testLocatorWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.TEST_LOCATOR));
            return testLocatorWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}

