package com.touchMind.web.controllers.admin.qa;

import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.dto.TestProfileDto;
import com.touchMind.core.mongo.dto.TestProfileWsDto;
import com.touchMind.core.mongo.model.TestProfile;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.TestProfileRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.TestProfileService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
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
    @Autowired
    private BaseService baseService;
//    @Autowired
//    private SubsidiaryRepository subsidiaryRepository;

    @PostMapping
    @ResponseBody
    public TestProfileWsDto getTestProfiles(@RequestBody TestProfileWsDto testProfileWsDto) {
        Pageable pageable = getPageable(testProfileWsDto.getPage(), testProfileWsDto.getSizePerPage(), testProfileWsDto.getSortDirection(), testProfileWsDto.getSortField());
        TestProfileDto testProfileDto = CollectionUtils.isNotEmpty(testProfileWsDto.getTestProfiles()) ? testProfileWsDto.getTestProfiles().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(testProfileDto, testProfileWsDto.getOperator());
        TestProfile testProfile = testProfileDto != null ? modelMapper.map(testProfileDto, TestProfile.class) : null;
        Page<TestProfile> page = isSearchActive(testProfile) != null ? testProfileRepository.findAll(Example.of(testProfile, exampleMatcher), pageable) : testProfileRepository.findAll(pageable);
        Type listType = new TypeToken<List<TestProfileDto>>() {
        }.getType();
        List<TestProfileDto> testProfiles = new ArrayList<>();
        for (TestProfile testProfile1 : page.getContent()) {
            TestProfileDto testProfileDto1 = modelMapper.map(testProfile1, TestProfileDto.class);
            String sub = testProfile1.getSubsidiary();
            if (StringUtils.isNotEmpty(sub)) {
                testProfileDto1.setSubsidiary(sub);
            }
            testProfiles.add(testProfileDto1);
        }
        testProfileWsDto.setTestProfiles(testProfiles);
        testProfileWsDto.setBaseUrl(ADMIN_PROFILE);
        testProfileWsDto.setTotalPages(page.getTotalPages());
        testProfileWsDto.setTotalRecords(page.getTotalElements());
        testProfileWsDto.setAttributeList(getConfiguredAttributes(testProfileWsDto.getNode()));
        testProfileWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.TEST_PROFILE));
        return testProfileWsDto;
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.TEST_PROFILE);
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody TestProfileWsDto testProfileWsDto) {
        return getConfiguredAttributes(testProfileWsDto.getNode());
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
        Type listType = new TypeToken<List<TestProfileDto>>() {
        }.getType();
        testProfileWsDto.setTestProfiles(modelMapper.map(testProfileRepository.findByStatusOrderByIdentifier(true), listType));
        return testProfileWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody TestProfileDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(testProfileRepository.findByIdentifier(recordId), TestProfileDto.class);
    }

    @GetMapping("/subsidiary")
    @ResponseBody
    public TestProfileWsDto getTestProfilesBySubsidiary() {
        TestProfileWsDto testProfileWsDto = new TestProfileWsDto();
        Type listType = new TypeToken<List<TestProfileDto>>() {
        }.getType();
        testProfileWsDto.setTestProfiles(modelMapper.map(testProfileRepository.findByStatusOrderByIdentifier(true), listType));
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
            testProfileService.deleteTestProfile(data.getIdentifier());
        }
        testProfileWsDto.setBaseUrl(ADMIN_PROFILE);
        testProfileWsDto.setMessage("Data deleted successfully!!");
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
            testProfiles.add(testProfileRepository.findByIdentifier(testProfileDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<TestProfileDto>>() {
        }.getType();
        request.setTestProfiles(modelMapper.map(testProfiles, listType));
        request.setBaseUrl(ADMIN_PROFILE);
        return request;
    }

    @GetMapping("/copy")
    @ResponseBody
    public TestProfileWsDto copy(@RequestParam("recordId") String recordId) {
        TestProfileWsDto testProfileWsDto = testProfileService.handleCopy(recordId);
        testProfileWsDto.setMessage("Test Profile cloned successfully!!");
        return testProfileWsDto;
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

    @PostMapping("/export")
    @ResponseBody
    public TestProfileWsDto uploadFile(@RequestBody TestProfileWsDto testProfileWsDto) {

        try {
            testProfileWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.TEST_PROFILE, testProfileWsDto.getHeaderFields()));
            return testProfileWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

}
