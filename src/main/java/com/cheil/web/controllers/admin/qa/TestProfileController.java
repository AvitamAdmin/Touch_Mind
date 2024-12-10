package com.cheil.web.controllers.admin.qa;

import com.cheil.core.mongo.dto.SearchDto;
import com.cheil.core.mongo.dto.TestProfileDto;
import com.cheil.core.mongo.dto.TestProfileWsDto;
import com.cheil.core.mongo.model.TestProfile;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.TestProfileRepository;
import com.cheil.core.service.TestProfileService;
import com.cheil.fileimport.service.FileExportService;
import com.cheil.fileimport.service.FileImportService;
import com.cheil.fileimport.strategies.EntityType;
import com.cheil.web.controllers.BaseController;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/admin/profile")
public class TestProfileController extends BaseController {

    public static final String ADMIN_PROFILE = "/admin/profile";
    Logger logger = LoggerFactory.getLogger(TestProfileController.class);

    @Autowired
    private TestProfileRepository testProfileRepository;

    @Autowired
    private TestProfileService testProfileService;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @ResponseBody
    public TestProfileWsDto getTestProfiles(@RequestBody TestProfileWsDto testProfileWsDto) {
        Pageable pageable = getPageable(testProfileWsDto.getPage(), testProfileWsDto.getSizePerPage(), testProfileWsDto.getSortDirection(), testProfileWsDto.getSortField());
        TestProfileDto testProfileDto = CollectionUtils.isNotEmpty(testProfileWsDto.getTestProfiles()) ? testProfileWsDto.getTestProfiles().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(testProfileDto, testProfileWsDto.getOperator());
        TestProfile testProfile = testProfileDto != null ? modelMapper.map(testProfileDto, TestProfile.class) : null;
        Page<TestProfile> page = isSearchActive(testProfile) != null ? testProfileRepository.findAll(Example.of(testProfile, exampleMatcher), pageable) : testProfileRepository.findAll(pageable);
        testProfileWsDto.setTestProfiles(modelMapper.map(page.getContent(), List.class));
        testProfileWsDto.setBaseUrl(ADMIN_PROFILE);
        testProfileWsDto.setTotalPages(page.getTotalPages());
        testProfileWsDto.setTotalRecords(page.getTotalElements());
        testProfileWsDto.setAttributeList(getConfiguredAttributes(testProfileWsDto.getNode()));
        return testProfileWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new TestProfile());
    }

    @GetMapping("/get")
    public TestProfileWsDto getTestProfiles() {
        TestProfileWsDto testProfileWsDto = new TestProfileWsDto();
        testProfileWsDto.setBaseUrl(ADMIN_PROFILE);
        testProfileWsDto.setTestProfiles(modelMapper.map(testProfileRepository.findByStatusOrderByIdentifier(true), List.class));
        return testProfileWsDto;
    }

    @GetMapping("/subsidiary")
    @ResponseBody
    public TestProfileWsDto getTestProfilesBySubsidiary() {
        TestProfileWsDto testProfileWsDto = new TestProfileWsDto();
        testProfileWsDto.setTestProfiles(modelMapper.map(testProfileRepository.findByStatusOrderByIdentifier(true), List.class));
        testProfileWsDto.setBaseUrl(ADMIN_PROFILE);
        return testProfileWsDto;
    }

    @GetMapping("/add")
    @ResponseBody
    public TestProfileWsDto getAddProfile() {
        TestProfileWsDto testProfileWsDto = new TestProfileWsDto();
        testProfileWsDto.setProfileLocatorList(testProfileService.getProfileLocators());
        testProfileWsDto.setBaseUrl(ADMIN_PROFILE);
        return testProfileWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public TestProfileWsDto getDeleteProfile(@RequestBody TestProfileWsDto testProfileWsDto) {
        for (TestProfileDto data : testProfileWsDto.getTestProfiles()) {
            testProfileService.deleteTestProfile(data.getRecordId());
        }
        testProfileWsDto.setBaseUrl(ADMIN_PROFILE);
        testProfileWsDto.setMessage("Data was deleted successfully!!");
        return testProfileWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public TestProfileWsDto updateTestProfile(@RequestBody TestProfileWsDto request) {
        return testProfileService.handleEdit(request);
    }


    @PostMapping("/getedits")
    @ResponseBody
    public TestProfileWsDto editMultiple(@RequestBody TestProfileWsDto request) {
        request.setBaseUrl(ADMIN_PROFILE);
        List<TestProfile> testProfiles = new ArrayList<>();
        for (TestProfileDto testProfileDto : request.getTestProfiles()) {
            testProfiles.add(testProfileRepository.findByRecordId(testProfileDto.getRecordId()));
        }
        request.setTestProfiles(modelMapper.map(testProfiles, List.class));
        request.setBaseUrl(ADMIN_PROFILE);
        return request;
    }

    @RequestMapping(value = "/copyProfile/{id}", method = RequestMethod.GET)
    @ResponseBody
    public TestProfileWsDto copyProfile(@RequestBody TestProfileWsDto request) {
        return testProfileService.handleCopy(request);
    }

    @PostMapping("/upload")
    @ResponseBody
    public TestProfileWsDto uploadFile(@RequestBody MultipartFile file) {
        TestProfileWsDto testProfileWsDto = new TestProfileWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.TEST_PROFILE, EntityConstants.TEST_PROFILE, testProfileWsDto);
            if (StringUtils.isEmpty(testProfileWsDto.getMessage())) {
                testProfileWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return testProfileWsDto;
    }

    @GetMapping("/export")
    @ResponseBody
    public TestProfileWsDto uploadFile() {
        TestProfileWsDto testProfileWsDto = new TestProfileWsDto();
        try {
            testProfileWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.TEST_PROFILE));
            return testProfileWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

}
