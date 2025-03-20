package com.touchMind.web.controllers.admin;

import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.dto.TestDataSubtypeDto;
import com.touchMind.core.mongo.dto.TestDataSubtypeWsDto;
import com.touchMind.core.mongo.model.TestDataSubtype;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.TestDataSubtypeRepository;
import com.touchMind.core.mongo.repository.TestDataTypeRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.TestDataSubTypeService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
import com.touchMind.web.controllers.BaseController;
import com.google.common.reflect.TypeToken;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/testdatasubtype")
public class TestDataSubtypeController extends BaseController {

    public static final String ADMIN_TEST_DATA_SUB_TYPE = "/admin/testdatasubtype";
    Logger logger = LoggerFactory.getLogger(TestDataSubtypeController.class);
//    @Autowired
//    private SubsidiaryService subsidiaryService;
    @Autowired
    private TestDataSubtypeRepository testDataSubtypeRepository;
    @Autowired
    private TestDataTypeRepository testDataTypeRepository;

    @Autowired
    private TestDataSubTypeService testDataSubTypeService;
    @Autowired
    private FileImportService fileImportService;
    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseService baseService;

    @PostMapping
    @ResponseBody
    public TestDataSubtypeWsDto getAll(@RequestBody TestDataSubtypeWsDto testDataSubtypeWsDto) {
        Pageable pageable = getPageable(testDataSubtypeWsDto.getPage(), testDataSubtypeWsDto.getSizePerPage(), testDataSubtypeWsDto.getSortDirection(), testDataSubtypeWsDto.getSortField());
        TestDataSubtypeDto testDataSubtypeDto = CollectionUtils.isNotEmpty(testDataSubtypeWsDto.getTestDataSubtypes()) ? testDataSubtypeWsDto.getTestDataSubtypes().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(testDataSubtypeDto, testDataSubtypeWsDto.getOperator());
        TestDataSubtype testDataSubtype = testDataSubtypeDto != null ? modelMapper.map(testDataSubtypeDto, TestDataSubtype.class) : null;
        Page<TestDataSubtype> page = isSearchActive(testDataSubtype) != null ? testDataSubtypeRepository.findAll(Example.of(testDataSubtype, exampleMatcher), pageable) : testDataSubtypeRepository.findAll(pageable);
        Type listType = new TypeToken<List<TestDataSubtypeDto>>() {
        }.getType();
        testDataSubtypeWsDto.setTestDataSubtypes(modelMapper.map(page.getContent(), listType));
        testDataSubtypeWsDto.setBaseUrl(ADMIN_TEST_DATA_SUB_TYPE);
        testDataSubtypeWsDto.setTotalPages(page.getTotalPages());
        testDataSubtypeWsDto.setTotalRecords(page.getTotalElements());
        testDataSubtypeWsDto.setAttributeList(getConfiguredAttributes(testDataSubtypeWsDto.getNode()));
        testDataSubtypeWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.TEST_DATA_SUB_TYPE));
        return testDataSubtypeWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody TestDataSubtypeWsDto testDataSubtypeWsDto) {
        return getConfiguredAttributes(testDataSubtypeWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new TestDataSubtype());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.TEST_DATA_SUB_TYPE);
    }

    @GetMapping("/get")
    @ResponseBody
    public TestDataSubtypeWsDto getActiveSubTypes() {
        TestDataSubtypeWsDto testDataSubtypeWsDto = new TestDataSubtypeWsDto();
        testDataSubtypeWsDto.setBaseUrl(ADMIN_TEST_DATA_SUB_TYPE);
        Type listType = new TypeToken<List<TestDataSubtypeDto>>() {
        }.getType();
        testDataSubtypeWsDto.setTestDataSubtypes(modelMapper.map(testDataSubtypeRepository.findByStatusOrderByIdentifier(true), listType));
        return testDataSubtypeWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public TestDataSubtypeWsDto handleEdit(@RequestBody TestDataSubtypeWsDto request) {
        return testDataSubTypeService.handleEdit(request);
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody TestDataSubtypeDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(testDataSubtypeRepository.findByIdentifier(recordId), TestDataSubtypeDto.class);
    }

    @PostMapping("/getedits")
    @ResponseBody
    public TestDataSubtypeWsDto editMultiple(@RequestBody TestDataSubtypeWsDto request) {
        TestDataSubtypeWsDto testDataSubtypeWsDto = new TestDataSubtypeWsDto();
        List<TestDataSubtype> testDataSubtypes = new ArrayList<>();
        for (TestDataSubtypeDto testDataSubtypeDto : request.getTestDataSubtypes()) {
            testDataSubtypes.add(testDataSubtypeRepository.findByIdentifier(testDataSubtypeDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<TestDataSubtypeDto>>() {
        }.getType();
        testDataSubtypeWsDto.setTestDataSubtypes(modelMapper.map(testDataSubtypes, listType));
        testDataSubtypeWsDto.setBaseUrl(ADMIN_TEST_DATA_SUB_TYPE);
        return testDataSubtypeWsDto;
    }


    @GetMapping("/add")
    @ResponseBody
    public TestDataSubtypeWsDto add() {
        TestDataSubtypeWsDto testDataSubtypeWsDto = new TestDataSubtypeWsDto();
        testDataSubtypeWsDto.setBaseUrl(ADMIN_TEST_DATA_SUB_TYPE);
        return testDataSubtypeWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public TestDataSubtypeWsDto delete(@RequestBody TestDataSubtypeWsDto testDataSubtypeWsDto) {
        for (TestDataSubtypeDto testDataSubtypeDto : testDataSubtypeWsDto.getTestDataSubtypes()) {
            testDataSubtypeRepository.deleteByIdentifier(testDataSubtypeDto.getIdentifier());
        }
        testDataSubtypeWsDto.setBaseUrl(ADMIN_TEST_DATA_SUB_TYPE);
        testDataSubtypeWsDto.setMessage("Data deleted successfully");
        return testDataSubtypeWsDto;
    }

    @RequestMapping(value = "/getTestDataSubtypes/{testDataTypeId}", method = RequestMethod.GET)
    public @ResponseBody Map<ObjectId, String> getTestDataSubtypes(@PathVariable("testDataTypeId") String testDataTypeId) {
        return testDataSubtypeRepository.findByTestDataType(testDataTypeId).stream()
                .collect(Collectors.toMap(TestDataSubtype::getId, TestDataSubtype::getIdentifier));
    }

    @PostMapping("/upload")
    public TestDataSubtypeWsDto uploadFile(@RequestBody MultipartFile file) {
        TestDataSubtypeWsDto testDataSubtypeWsDto = new TestDataSubtypeWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.TEST_DATA_SUB_TYPE, EntityConstants.TEST_DATA_SUB_TYPE, testDataSubtypeWsDto);
            if (StringUtils.isEmpty(testDataSubtypeWsDto.getMessage())) {
                testDataSubtypeWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return testDataSubtypeWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public TestDataSubtypeWsDto uploadFile(@RequestBody TestDataSubtypeWsDto testDataSubtypeWsDto) {

        try {
            testDataSubtypeWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.TEST_DATA_SUB_TYPE, testDataSubtypeWsDto.getHeaderFields()));
            return testDataSubtypeWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
