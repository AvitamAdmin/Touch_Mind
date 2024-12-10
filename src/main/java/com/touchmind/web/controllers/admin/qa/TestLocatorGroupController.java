package com.touchmind.web.controllers.admin.qa;

import com.touchmind.core.mongo.dto.LocatorGroupDto;
import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.dto.TestLocatorGroupDto;
import com.touchmind.core.mongo.dto.TestLocatorGroupWsDto;
import com.touchmind.core.mongo.model.Node;
import com.touchmind.core.mongo.model.SourceTargetMapping;
import com.touchmind.core.mongo.model.SourceTargetParamMapping;
import com.touchmind.core.mongo.model.TestLocatorGroup;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.NodeRepository;
import com.touchmind.core.mongo.repository.SourceTargetMappingRepository;
import com.touchmind.core.mongo.repository.TestLocatorGroupRepository;
import com.touchmind.core.service.LocatorGroupService;
import com.touchmind.core.service.LocatorService;
import com.touchmind.core.service.TestLocatorGroupService;
import com.touchmind.fileimport.service.FileExportService;
import com.touchmind.fileimport.service.FileImportService;
import com.touchmind.fileimport.strategies.EntityType;
import com.touchmind.form.LocatorForm;
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
import java.util.ArrayList;
import java.util.List;

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

    @PostMapping
    @ResponseBody
    public TestLocatorGroupWsDto getTestLocators(@RequestBody TestLocatorGroupWsDto testLocatorGroupWsDto) {
        Pageable pageable = getPageable(testLocatorGroupWsDto.getPage(), testLocatorGroupWsDto.getSizePerPage(), testLocatorGroupWsDto.getSortDirection(), testLocatorGroupWsDto.getSortField());
        TestLocatorGroupDto testLocatorGroupDto = CollectionUtils.isNotEmpty(testLocatorGroupWsDto.getTestLocatorGroups()) ? testLocatorGroupWsDto.getTestLocatorGroups().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(testLocatorGroupDto, testLocatorGroupWsDto.getOperator());
        TestLocatorGroup testLocatorGroup = testLocatorGroupDto != null ? modelMapper.map(testLocatorGroupDto, TestLocatorGroup.class) : null;
        Page<TestLocatorGroup> page = isSearchActive(testLocatorGroup) != null ? testLocatorGroupRepository.findAll(Example.of(testLocatorGroup, exampleMatcher), pageable) : testLocatorGroupRepository.findAll(pageable);
        testLocatorGroupWsDto.setTestLocatorGroups(modelMapper.map(page.getContent(), List.class));
        testLocatorGroupWsDto.setBaseUrl(ADMIN_LOCATOR_GROUP);
        testLocatorGroupWsDto.setTotalPages(page.getTotalPages());
        testLocatorGroupWsDto.setTotalRecords(page.getTotalElements());
        testLocatorGroupWsDto.setAttributeList(getConfiguredAttributes(testLocatorGroupWsDto.getNode()));
        return testLocatorGroupWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new TestLocatorGroup());
    }

    @GetMapping("/get")
    public TestLocatorGroupWsDto getTestLocators() {
        TestLocatorGroupWsDto testLocatorGroupWsDto = new TestLocatorGroupWsDto();
        testLocatorGroupWsDto.setTestLocatorGroups(modelMapper.map(testLocatorGroupRepository.findByStatusOrderByIdentifier(true), List.class));
        testLocatorGroupWsDto.setBaseUrl(ADMIN_LOCATOR_GROUP);
        return testLocatorGroupWsDto;
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

    @RequestMapping(value = "/copyForm/{id}", method = RequestMethod.GET)
    public @ResponseBody String copyLocatorGroup(@PathVariable("id") String id) {
        LocatorGroupDto locatorGroupDto = locatorGroupService.editLocatorGroup(id);
        locatorGroupDto.setIdentifier(locatorGroupDto.getIdentifier() + "_COPY");
        locatorGroupDto.setRecordId(null);
        locatorGroupService.addLocatorGroup(locatorGroupDto);
        return "success";
    }

    @PostMapping("/delete")
    @ResponseBody
    public TestLocatorGroupWsDto deleteLocator(@RequestBody TestLocatorGroupWsDto testLocatorGroupWsDto) {
        for (TestLocatorGroupDto testLocatorGroupDto : testLocatorGroupWsDto.getTestLocatorGroups()) {
            testLocatorGroupRepository.deleteByRecordId(testLocatorGroupDto.getRecordId());
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
        List<TestLocatorGroup> testLocatorGroups = new ArrayList<>();
        for (TestLocatorGroupDto testLocatorGroupDto : testLocatorGroupDtoList) {
            testLocatorGroups.add(testLocatorGroupRepository.findByRecordId(testLocatorGroupDto.getRecordId()));
        }
        request.setTestLocatorGroups(modelMapper.map(testLocatorGroups, List.class));
        request.setRedirectUrl("/admin/locatorGroup");
        request.setBaseUrl(ADMIN_LOCATOR_GROUP);
        return request;
    }

    @RequestMapping(value = "/getMappingParamsForNodeId", method = RequestMethod.GET)
    public @ResponseBody List<String> getMappingParamsForNodeId(@RequestBody @RequestParam("nodeId") String nodeId) {
        List<String> params = new ArrayList<>();
        Node node = nodeRepository.findByRecordId(nodeId);
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
        locatorService.saveLoctor(locatorForm);
        return "redirect:/admin/locatorGroup/edit?id=" + locatorForm.getGroupId();
    }

    @GetMapping("/export")
    @ResponseBody
    public TestLocatorGroupWsDto uploadFile() {
        TestLocatorGroupWsDto testLocatorGroupWsDto = new TestLocatorGroupWsDto();
        try {
            testLocatorGroupWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.TEST_LOCATOR_GROUP));
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
