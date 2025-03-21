package com.touchMind.web.controllers.admin;

import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.dto.TestDataTypeDto;
import com.touchMind.core.mongo.dto.TestDataTypeWsDto;
import com.touchMind.core.mongo.model.TestDataType;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.TestDataTypeRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.TestDataTypeService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
import com.touchMind.web.controllers.BaseController;
import com.touchMind.web.controllers.catalog.CatalogController;
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
import java.util.List;


@RestController
@RequestMapping("/admin/testdatatype")
public class TestDataTypeController extends BaseController {

    public static final String ADMIN_TEST_DATA_TYPE = "/admin/testdatatype";
    Logger logger = LoggerFactory.getLogger(CatalogController.class);
    @Autowired
    private TestDataTypeService testDataTypeService;
    @Autowired
    private TestDataTypeRepository testDataTypeRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileImportService fileImportService;
    @Autowired
    private FileExportService fileExportService;
    @Autowired
    private BaseService baseService;

    @PostMapping
    @ResponseBody
    public TestDataTypeWsDto getAll(@RequestBody TestDataTypeWsDto testDataTypeWsDto) {
        Pageable pageable = getPageable(testDataTypeWsDto.getPage(), testDataTypeWsDto.getSizePerPage(), testDataTypeWsDto.getSortDirection(), testDataTypeWsDto.getSortField());
        TestDataTypeDto testDataTypeDto = CollectionUtils.isNotEmpty(testDataTypeWsDto.getTestDataTypes()) ? testDataTypeWsDto.getTestDataTypes().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(testDataTypeDto, testDataTypeWsDto.getOperator());
        TestDataType testDataType = testDataTypeDto != null ? modelMapper.map(testDataTypeDto, TestDataType.class) : null;
        Page<TestDataType> page = isSearchActive(testDataType) != null ? testDataTypeRepository.findAll(Example.of(testDataType, exampleMatcher), pageable) : testDataTypeRepository.findAll(pageable);
        Type listType = new TypeToken<List<TestDataTypeDto>>() {
        }.getType();
        testDataTypeWsDto.setTestDataTypes(modelMapper.map(page.getContent(), listType));
        testDataTypeWsDto.setBaseUrl(ADMIN_TEST_DATA_TYPE);
        testDataTypeWsDto.setTotalPages(page.getTotalPages());
        testDataTypeWsDto.setTotalRecords(page.getTotalElements());
        testDataTypeWsDto.setAttributeList(getConfiguredAttributes(testDataTypeWsDto.getNode()));
        testDataTypeWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.TEST_DATA_TYPE));
        return testDataTypeWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody TestDataTypeWsDto testDataTypeWsDto) {
        return getConfiguredAttributes(testDataTypeWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new TestDataType());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.TEST_DATA_TYPE);
    }

    @GetMapping("/get")
    @ResponseBody
    public TestDataTypeWsDto getActiveDataTypes() {
        TestDataTypeWsDto testDataTypeWsDto = new TestDataTypeWsDto();
        testDataTypeWsDto.setBaseUrl(ADMIN_TEST_DATA_TYPE);
        Type listType = new TypeToken<List<TestDataTypeDto>>() {
        }.getType();
        testDataTypeWsDto.setTestDataTypes(modelMapper.map(testDataTypeRepository.findByStatusOrderByIdentifier(true), listType));
        return testDataTypeWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody TestDataTypeDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(testDataTypeRepository.findByIdentifier(recordId), TestDataTypeDto.class);
    }


    @PostMapping("/edit")
    @ResponseBody
    public TestDataTypeWsDto handleEdit(@RequestBody TestDataTypeWsDto request) {
        return testDataTypeService.handleEdit(request);
    }

    @GetMapping("/add")
    @ResponseBody
    public TestDataTypeWsDto add() {
        TestDataTypeWsDto testDataTypeWsDto = new TestDataTypeWsDto();
        testDataTypeWsDto.setBaseUrl(ADMIN_TEST_DATA_TYPE);
        return testDataTypeWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public TestDataTypeWsDto editMultiple(@RequestBody TestDataTypeWsDto request) {
        TestDataTypeWsDto testDataTypeWsDto = new TestDataTypeWsDto();
        List<TestDataType> testDatatypes = new ArrayList<>();
        for (TestDataTypeDto testDataTypeDto : request.getTestDataTypes()) {
            testDatatypes.add(testDataTypeRepository.findByIdentifier(testDataTypeDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<TestDataTypeDto>>() {
        }.getType();
        testDataTypeWsDto.setTestDataTypes(modelMapper.map(testDatatypes, listType));
        testDataTypeWsDto.setRedirectUrl("");
        testDataTypeWsDto.setBaseUrl(ADMIN_TEST_DATA_TYPE);
        return testDataTypeWsDto;
    }


    @PostMapping("/delete")
    @ResponseBody
    public TestDataTypeWsDto delete(@RequestBody TestDataTypeWsDto testDataTypeWsDto) {
        for (TestDataTypeDto testDataTypeDto : testDataTypeWsDto.getTestDataTypes()) {
            testDataTypeRepository.deleteByIdentifier(testDataTypeDto.getIdentifier());
        }
        testDataTypeWsDto.setBaseUrl(ADMIN_TEST_DATA_TYPE);
        testDataTypeWsDto.setMessage("Data deleted successfully");
        return testDataTypeWsDto;
    }

    @PostMapping("/upload")
    public TestDataTypeWsDto uploadFile(@RequestParam("file") MultipartFile file) {
        TestDataTypeWsDto testDataTypeWsDto = new TestDataTypeWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.TEST_DATA_TYPE, EntityConstants.TEST_DATA_TYPE, testDataTypeWsDto);
            if (StringUtils.isEmpty(testDataTypeWsDto.getMessage())) {
                testDataTypeWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return testDataTypeWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public TestDataTypeWsDto uploadFile(@RequestBody TestDataTypeWsDto testDataTypeWsDto) {

        try {
            testDataTypeWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.TEST_DATA_TYPE, testDataTypeWsDto.getHeaderFields()));
            return testDataTypeWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
