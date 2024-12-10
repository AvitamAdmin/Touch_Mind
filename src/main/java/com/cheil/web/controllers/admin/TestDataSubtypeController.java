package com.cheil.web.controllers.admin;

import com.cheil.core.mongo.dto.SearchDto;
import com.cheil.core.mongo.dto.TestDataSubtypeDto;
import com.cheil.core.mongo.dto.TestDataSubtypeWsDto;
import com.cheil.core.mongo.model.TestDataSubtype;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.TestDataSubtypeRepository;
import com.cheil.core.mongo.repository.TestDataTypeRepository;
import com.cheil.core.service.SubsidiaryService;
import com.cheil.core.service.TestDataSubTypeService;
import com.cheil.fileimport.service.FileExportService;
import com.cheil.fileimport.service.FileImportService;
import com.cheil.fileimport.strategies.EntityType;
import com.cheil.web.controllers.BaseController;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/testdatasubtype")
public class TestDataSubtypeController extends BaseController {

    public static final String ADMIN_TEST_DATA_SUB_TYPE = "/admin/testdatasubtype";
    Logger logger = LoggerFactory.getLogger(TestDataSubtypeController.class);
    @Autowired
    private SubsidiaryService subsidiaryService;
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

    @PostMapping
    @ResponseBody
    public TestDataSubtypeWsDto getAll(@RequestBody TestDataSubtypeWsDto testDataSubtypeWsDto) {
        Pageable pageable = getPageable(testDataSubtypeWsDto.getPage(), testDataSubtypeWsDto.getSizePerPage(), testDataSubtypeWsDto.getSortDirection(), testDataSubtypeWsDto.getSortField());
        TestDataSubtypeDto testDataSubtypeDto = CollectionUtils.isNotEmpty(testDataSubtypeWsDto.getTestDataSubtypes()) ? testDataSubtypeWsDto.getTestDataSubtypes().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(testDataSubtypeDto, testDataSubtypeWsDto.getOperator());
        TestDataSubtype testDataSubtype = testDataSubtypeDto != null ? modelMapper.map(testDataSubtypeDto, TestDataSubtype.class) : null;
        Page<TestDataSubtype> page = isSearchActive(testDataSubtype) != null ? testDataSubtypeRepository.findAll(Example.of(testDataSubtype, exampleMatcher), pageable) : testDataSubtypeRepository.findAll(pageable);
        testDataSubtypeWsDto.setTestDataSubtypes(modelMapper.map(page.getContent(), List.class));
        testDataSubtypeWsDto.setBaseUrl(ADMIN_TEST_DATA_SUB_TYPE);
        testDataSubtypeWsDto.setTotalPages(page.getTotalPages());
        testDataSubtypeWsDto.setTotalRecords(page.getTotalElements());
        testDataSubtypeWsDto.setAttributeList(getConfiguredAttributes(testDataSubtypeWsDto.getNode()));
        return testDataSubtypeWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new TestDataSubtype());
    }

    @GetMapping("/get")
    @ResponseBody
    public TestDataSubtypeWsDto getActiveSubTypes() {
        TestDataSubtypeWsDto testDataSubtypeWsDto = new TestDataSubtypeWsDto();
        testDataSubtypeWsDto.setBaseUrl(ADMIN_TEST_DATA_SUB_TYPE);
        testDataSubtypeWsDto.setTestDataSubtypes(modelMapper.map(testDataSubtypeRepository.findByStatusOrderByIdentifier(true), List.class));
        return testDataSubtypeWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public TestDataSubtypeWsDto handleEdit(@RequestBody TestDataSubtypeWsDto request) {
        return testDataSubTypeService.handleEdit(request);
    }

    @PostMapping("/getedits")
    @ResponseBody
    public TestDataSubtypeWsDto editMultiple(@RequestBody TestDataSubtypeWsDto request) {
        TestDataSubtypeWsDto testDataSubtypeWsDto = new TestDataSubtypeWsDto();
        List<TestDataSubtype> testDataSubtypes = new ArrayList<>();
        for (TestDataSubtypeDto testDataSubtypeDto : request.getTestDataSubtypes()) {
            testDataSubtypes.add(testDataSubtypeRepository.findByRecordId(testDataSubtypeDto.getRecordId()));
        }
        testDataSubtypeWsDto.setTestDataSubtypes(modelMapper.map(testDataSubtypes, List.class));
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
            testDataSubtypeRepository.deleteByRecordId(testDataSubtypeDto.getRecordId());
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

    @GetMapping("/export")
    @ResponseBody
    public TestDataSubtypeWsDto uploadFile() {
        TestDataSubtypeWsDto testDataSubtypeWsDto = new TestDataSubtypeWsDto();
        try {
            testDataSubtypeWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.TEST_DATA_SUB_TYPE));
            return testDataSubtypeWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
