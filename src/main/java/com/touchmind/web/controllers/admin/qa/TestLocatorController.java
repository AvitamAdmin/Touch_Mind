package com.touchmind.web.controllers.admin.qa;

import com.touchmind.core.mongo.dto.TestLocatorDto;
import com.touchmind.core.mongo.dto.TestLocatorWsDto;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.core.mongo.repository.TestLocatorRepository;
import com.touchmind.core.service.LocatorService;
import com.touchmind.core.service.TestLocatorService;
import com.touchmind.form.LocatorForm;
import com.touchmind.form.LocatorSelectorDto;
import com.touchmind.qa.service.impl.SelectorServiceImpl;
import com.touchmind.web.controllers.BaseController;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

//    @Autowired
//    private SiteRepository siteRepository;

//    @Autowired
//    private FileImportService fileImportService;
//
//    @Autowired
//    private FileExportService fileExportService;//

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestLocatorService testLocatorService;

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
       // testLocatorWsDto.setAttributeList(getConfiguredAttributes(testLocatorWsDto.getNode()));
        return testLocatorWsDto;
    }

//    @GetMapping("/getAdvancedSearch")
//    @ResponseBody
//    public List<SearchDto> getSearchAttributes() {
//        return getGroupedParentAndChildAttributes(new TestLocator());
//    }

    @GetMapping("/get")
    public TestLocatorWsDto getTestLocators() {
        TestLocatorWsDto testLocatorWsDto = new TestLocatorWsDto();
        testLocatorWsDto.setTestLocators(modelMapper.map(testLocatorRepository.findByStatusOrderByIdentifier(true), List.class));
        testLocatorWsDto.setBaseUrl(ADMIN_LOCATOR);
        return testLocatorWsDto;
    }

//    @GetMapping("/migrate")
//    @ResponseBody
//    public void migrateData() {
//        List<TestLocator> testLocators = testLocatorRepository.findAll();
//        for (TestLocator locator : testLocators) {
//            List<Site> sites = siteRepository.findByStatusOrderByIdentifier(true);
//            SortedMap<String, LocatorSelectorDto> locatorSelectorFormMap = new TreeMap<>();
//            for (Site site : sites) {
//                LocatorSelectorDto locatorSelectorDto = new LocatorSelectorDto();
//                //locatorSelectorDto.setXpathSelector(locator.getUiSelector(site.getIdentifier()));
//                //locatorSelectorDto.setInputData(locator.getInputData(site.getIdentifier()));
//                locatorSelectorFormMap.put(site.getIdentifier(), locatorSelectorDto);
//                locator.setUiLocatorSelector(locatorSelectorFormMap);
//            }
//            testLocatorRepository.save(locator);
//        }
//    }

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
        LocatorForm locatorForm = locatorService.editLocator(id);
        locatorForm.setIdentifier(locatorForm.getIdentifier() + "_" + ObjectId.get() + "_COPY");
        locatorForm.setRecordId(null);
        locatorService.addLocator(locatorForm);
        return "success";
    }
//
//    @PostMapping("/upload")
//    @ResponseBody
//    public TestLocatorWsDto uploadFile(@RequestBody MultipartFile file) {
//        TestLocatorWsDto testLocatorWsDto = new TestLocatorWsDto();
//        try {
//            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.TEST_LOCATOR, EntityConstants.TEST_LOCATOR, testLocatorWsDto);
//            if (StringUtils.isEmpty(testLocatorWsDto.getMessage())) {
//                testLocatorWsDto.setMessage("File uploaded successfully!!");
//            }
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
//        return testLocatorWsDto;
//    }
//
//    @GetMapping("/export")
//    @ResponseBody
//    public TestLocatorWsDto uploadFile() {
//        TestLocatorWsDto testLocatorWsDto = new TestLocatorWsDto();
//        try {
//            testLocatorWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.TEST_LOCATOR));
//            return testLocatorWsDto;
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//            return null;
//        }
//    }
}

