package com.touchMind.web.controllers.admin.qa;

import com.touchMind.core.mongo.dto.LocatorGroupDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.dto.TestLocatorGroupDto;
import com.touchMind.core.mongo.dto.TestLocatorGroupWsDto;
import com.touchMind.core.mongo.model.LocatorPriority;
import com.touchMind.core.mongo.model.Node;
import com.touchMind.core.mongo.model.SourceTargetMapping;
import com.touchMind.core.mongo.model.SourceTargetParamMapping;
import com.touchMind.core.mongo.model.TestLocatorGroup;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.LocatorGroupRepository;
import com.touchMind.core.mongo.repository.NodeRepository;
import com.touchMind.core.mongo.repository.SourceTargetMappingRepository;
import com.touchMind.core.mongo.repository.TestLocatorGroupRepository;
import com.touchMind.core.mongo.repository.TestLocatorRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.LocatorGroupService;
import com.touchMind.core.service.LocatorService;
import com.touchMind.core.service.TestLocatorGroupService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
import com.touchMind.form.LocatorForm;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/admin/locatorGroup")
public class TestLocatorGroupController extends BaseController {

    public static final String ADMIN_LOCATOR_GROUP = "/admin/locatorGroup";
    Logger logger = LoggerFactory.getLogger(TestLocatorGroupController.class);

    @Autowired
    private TestLocatorGroupService testLocatorGroupService;

    @Autowired
    private LocatorGroupService locatorGroupService;

    @Autowired
    private LocatorService locatorService;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private SourceTargetMappingRepository sourceTargetMappingRepository;

    @Autowired
    private TestLocatorGroupRepository testLocatorGroupRepository;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;
    @Autowired
    private LocatorGroupRepository locatorGroupRepository;
//    @Autowired
//    private SubsidiaryRepository subsidiaryRepository;
    @Autowired
    private TestLocatorRepository testLocatorRepository;

    @PostMapping
    @ResponseBody
    public TestLocatorGroupWsDto getTestLocators(@RequestBody TestLocatorGroupWsDto testLocatorGroupWsDto) {
        Pageable pageable = getPageable(testLocatorGroupWsDto.getPage(), testLocatorGroupWsDto.getSizePerPage(), testLocatorGroupWsDto.getSortDirection(), testLocatorGroupWsDto.getSortField());
        TestLocatorGroupDto testLocatorGroupDto = CollectionUtils.isNotEmpty(testLocatorGroupWsDto.getTestLocatorGroups()) ? testLocatorGroupWsDto.getTestLocatorGroups().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(testLocatorGroupDto, testLocatorGroupWsDto.getOperator());
        TestLocatorGroup testLocatorGroup = testLocatorGroupDto != null ? modelMapper.map(testLocatorGroupDto, TestLocatorGroup.class) : null;
        Page<TestLocatorGroup> page = isSearchActive(testLocatorGroup) != null ? testLocatorGroupRepository.findAll(Example.of(testLocatorGroup, exampleMatcher), pageable) : testLocatorGroupRepository.findAll(pageable);
        Type listType = new TypeToken<List<TestLocatorGroupDto>>() {
        }.getType();
        List<TestLocatorGroupDto> testLocatorGroups = new ArrayList<>();
        for (TestLocatorGroup testLocatorGroup1 : page.getContent()) {
            TestLocatorGroupDto testLocatorGroupDto1 = modelMapper.map(testLocatorGroup1, TestLocatorGroupDto.class);
            String subsidiary = testLocatorGroup1.getSubsidiary();
            if (StringUtils.isNotEmpty(subsidiary)) {
                testLocatorGroupDto1.setSubsidiary(subsidiary);
            }
            testLocatorGroups.add(testLocatorGroupDto1);
        }
        testLocatorGroupWsDto.setTestLocatorGroups(testLocatorGroups);
        testLocatorGroupWsDto.setBaseUrl(ADMIN_LOCATOR_GROUP);
        testLocatorGroupWsDto.setTotalPages(page.getTotalPages());
        testLocatorGroupWsDto.setTotalRecords(page.getTotalElements());
        testLocatorGroupWsDto.setAttributeList(getConfiguredAttributes(testLocatorGroupWsDto.getNode()));
        testLocatorGroupWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.TEST_LOCATOR_GROUP));
        return testLocatorGroupWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody TestLocatorGroupWsDto testLocatorGroupWsDto) {
        return getConfiguredAttributes(testLocatorGroupWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new TestLocatorGroup());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.TEST_LOCATOR_GROUP);
    }

    @GetMapping("/copy")
    @ResponseBody
    public TestLocatorGroupWsDto copy(@RequestParam("recordId") String recordId) {
        TestLocatorGroupWsDto locatorGroupWsDto = new TestLocatorGroupWsDto();
        TestLocatorGroup locatorGroup = testLocatorGroupRepository.findByIdentifier(recordId);
        TestLocatorGroupDto locatorGroupDto = modelMapper.map(locatorGroup, TestLocatorGroupDto.class);
        locatorGroupDto.setIdentifier(null);
        locatorGroupDto.setCreationTime(new Date());
        locatorGroupDto.setLastModified(new Date());
        locatorGroupDto.setIdentifier("Copy_" + locatorGroup.getIdentifier());
        TestLocatorGroup clonedLocatorGroup = modelMapper.map(locatorGroupDto, TestLocatorGroup.class);
        testLocatorGroupRepository.save(clonedLocatorGroup);
        String id = String.valueOf(clonedLocatorGroup.getId().getTimestamp());
        if (testLocatorGroupRepository.findByIdentifier(id) != null) {
            id = id + new Random().nextInt(24565);
        }
        clonedLocatorGroup.setIdentifier(id);
        clonedLocatorGroup.setIdentifier("Copy_" + id + locatorGroup.getIdentifier());
        testLocatorGroupRepository.save(clonedLocatorGroup);
        locatorGroupWsDto.setMessage("Testcase cloned successfully!!");
        return locatorGroupWsDto;
    }

    @GetMapping("/get")
    public TestLocatorGroupWsDto getTestLocators() {
        TestLocatorGroupWsDto testLocatorGroupWsDto = new TestLocatorGroupWsDto();
        Type listType = new TypeToken<List<TestLocatorGroupDto>>() {
        }.getType();
        testLocatorGroupWsDto.setTestLocatorGroups(modelMapper.map(testLocatorGroupRepository.findByStatusOrderByIdentifier(true), listType));
        testLocatorGroupWsDto.setBaseUrl(ADMIN_LOCATOR_GROUP);
        return testLocatorGroupWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody TestLocatorGroupDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(testLocatorGroupRepository.findByIdentifier(recordId), TestLocatorGroupDto.class);
    }

    @GetMapping("/add")
    @ResponseBody
    public TestLocatorGroupWsDto getLocatorGroupForm() {
        TestLocatorGroupWsDto testLocatorGroupWsDto = new TestLocatorGroupWsDto();
        testLocatorGroupWsDto.setBaseUrl(ADMIN_LOCATOR_GROUP);
        testLocatorGroupWsDto.setExistingConditionCount(0);
        testLocatorGroupWsDto.setExistingParamsCount(0);
        Node toolkit = nodeRepository.findByPath("/toolkit");
        testLocatorGroupWsDto.setBaseUrl(ADMIN_LOCATOR_GROUP);
        testLocatorGroupWsDto.setConditionOperators(locatorGroupService.getConditionOperators());
        return testLocatorGroupWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public TestLocatorGroupWsDto addLocator(@RequestBody TestLocatorGroupWsDto request) {
        return testLocatorGroupService.handleEdit(request);
    }

    @PostMapping("/delete")
    @ResponseBody
    public TestLocatorGroupWsDto deleteLocator(@RequestBody TestLocatorGroupWsDto testLocatorGroupWsDto) {
        for (TestLocatorGroupDto testLocatorGroupDto : testLocatorGroupWsDto.getTestLocatorGroups()) {
            testLocatorGroupRepository.deleteByIdentifier(testLocatorGroupDto.getIdentifier());
        }
        testLocatorGroupWsDto.setMessage("Data deleted successfully!!");
        testLocatorGroupWsDto.setBaseUrl(ADMIN_LOCATOR_GROUP);
        return testLocatorGroupWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public TestLocatorGroupWsDto editMultiple(@RequestBody TestLocatorGroupWsDto request) {
        List<TestLocatorGroupDto> testLocatorGroupDtoList = request.getTestLocatorGroups();
        request.setBaseUrl(ADMIN_LOCATOR_GROUP);
        List<TestLocatorGroupDto> testLocatorGroups = new ArrayList<>();
        for (TestLocatorGroupDto testLocatorGroupDto : testLocatorGroupDtoList) {
            TestLocatorGroupDto testLocatorGroupDto1 = modelMapper.map(testLocatorGroupRepository.findByIdentifier(testLocatorGroupDto.getIdentifier()), TestLocatorGroupDto.class);
            if (CollectionUtils.isNotEmpty(testLocatorGroupDto1.getTestLocators())) {
                List<LocatorPriority> locatorPriorities = testLocatorGroupDto1.getTestLocators().stream().filter(locatorPriority -> StringUtils.isNotEmpty(locatorPriority.getGroupId())).toList();
                for (LocatorPriority locatorPriority : locatorPriorities) {
                    LocatorGroupDto locatorGroupDto = modelMapper.map(locatorGroupRepository.findByIdentifier(locatorPriority.getGroupId()), LocatorGroupDto.class);
                    if (locatorPriority.getLocatorGroup() != null) {
                        locatorGroupDto.setClearHardFailed(locatorPriority.getLocatorGroup().isClearHardFailed());
                    }
                    locatorPriority.setLocatorGroup(locatorGroupDto);
                }
            }
            testLocatorGroups.add(testLocatorGroupDto1);
        }
        request.setTestLocatorGroups(testLocatorGroups);
        request.setRedirectUrl("/admin/locatorGroup");
        request.setBaseUrl(ADMIN_LOCATOR_GROUP);
        return request;
    }

    @RequestMapping(value = "/getMappingParamsForNodeId", method = RequestMethod.GET)
    public @ResponseBody List<String> getMappingParamsForNodeId(@RequestBody @RequestParam("nodeId") String nodeId) {
        List<String> params = new ArrayList<>();
        Node node = nodeRepository.findByIdentifier(nodeId);
        if (node != null) {
            SourceTargetMapping sourceTargetMapping = sourceTargetMappingRepository.findByNode(nodeId);
            if (sourceTargetMapping != null) {
                List<SourceTargetParamMapping> sourceTargetParamMappingList = sourceTargetMapping.getSourceTargetParamMappings();
                sourceTargetParamMappingList.forEach(sourceTargetParamMapping -> {
                    params.add(sourceTargetParamMapping.getParam());
                });
            }
        }
        return params;
    }

    @GetMapping("/editLocator/{id}")
    @ResponseBody
    public ModelAndView editLocator(@PathVariable("id") ObjectId id, @RequestParam("groupId") String groupId, Model model) {
        model.addAttribute("locatorForm", locatorService.editLocator(String.valueOf(id.getTimestamp())));
        model.addAttribute("methods", locatorService.getMethodNames());
        model.addAttribute("groupId", groupId);
        return new ModelAndView("qa/testLocatorPopup");
    }

    @PostMapping("/updateLocator")
    @ResponseBody
    public String updateLocator(@ModelAttribute("locatorForm") LocatorForm locatorForm) {
        locatorService.saveLocator(locatorForm);
        return "redirect:/admin/locatorGroup/edit?id=" + locatorForm.getGroupId();
    }

    @PostMapping("/export")
    @ResponseBody
    public TestLocatorGroupWsDto uploadFile(@RequestBody TestLocatorGroupWsDto testLocatorGroupWsDto) {

        try {
            testLocatorGroupWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.TEST_LOCATOR_GROUP, testLocatorGroupWsDto.getHeaderFields()));
            return testLocatorGroupWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @PostMapping("/upload")
    public TestLocatorGroupWsDto uploadFile(@RequestBody MultipartFile file) {
        TestLocatorGroupWsDto testLocatorGroupWsDto = new TestLocatorGroupWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.TEST_LOCATOR_GROUP, EntityConstants.TEST_LOCATOR_GROUP, testLocatorGroupWsDto);
            if (StringUtils.isEmpty(testLocatorGroupWsDto.getMessage())) {
                testLocatorGroupWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return testLocatorGroupWsDto;
    }
}
