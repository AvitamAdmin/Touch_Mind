package com.touchMind.web.controllers.admin;

import com.touchMind.core.mongo.dto.MessageResourceDto;
import com.touchMind.core.mongo.dto.MessageResourceWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.MessageResource;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.MessageResourceRepository;
import com.touchMind.core.mongo.repository.QaTestPlanRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.MessageResourceService;
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
@RequestMapping("/admin/messages")
public class MessageResourceController extends BaseController {

    public static final String ADMIN_MESSAGES = "/admin/messages";
    Logger logger = LoggerFactory.getLogger(MessageResourceController.class);
    @Autowired
    private MessageResourceService messageResourceService;

    @Autowired
    private QaTestPlanRepository testPlanRepository;

    @Autowired
    private MessageResourceRepository messageResourceRepository;

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
    public MessageResourceWsDto getAllMessages(@RequestBody MessageResourceWsDto messageResourceWsDto) {
        Pageable pageable = getPageable(messageResourceWsDto.getPage(), messageResourceWsDto.getSizePerPage(), messageResourceWsDto.getSortDirection(), messageResourceWsDto.getSortField());
        MessageResourceDto messageResourceDto = CollectionUtils.isNotEmpty(messageResourceWsDto.getMessageResources()) ? messageResourceWsDto.getMessageResources().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(messageResourceDto, messageResourceWsDto.getOperator());
        MessageResource messageResource = messageResourceDto != null ? modelMapper.map(messageResourceDto, MessageResource.class) : null;
        Page<MessageResource> page = isSearchActive(messageResource) != null ? messageResourceRepository.findAll(Example.of(messageResource, exampleMatcher), pageable) : messageResourceRepository.findAll(pageable);
        messageResourceWsDto.setBaseUrl(ADMIN_MESSAGES);
        Type listType = new TypeToken<List<MessageResourceDto>>() {
        }.getType();
        messageResourceWsDto.setMessageResources(modelMapper.map(page.getContent(), listType));
        messageResourceWsDto.setTotalPages(page.getTotalPages());
        messageResourceWsDto.setTotalRecords(page.getTotalElements());
        messageResourceWsDto.setAttributeList(getConfiguredAttributes(messageResourceWsDto.getNode()));
        messageResourceWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.MESSAGES));
        return messageResourceWsDto;
    }


    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody MessageResourceWsDto messageResourceWsDto) {
        return getConfiguredAttributes(messageResourceWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new MessageResource());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.MESSAGES);
    }

    @GetMapping("/get")
    @ResponseBody
    public MessageResourceWsDto getActiveMessages() {
        MessageResourceWsDto messageResourceWsDto = new MessageResourceWsDto();
        messageResourceWsDto.setBaseUrl(ADMIN_MESSAGES);
        Type listType = new TypeToken<List<MessageResourceDto>>() {
        }.getType();
        messageResourceWsDto.setMessageResources(modelMapper.map(messageResourceRepository.findByStatusOrderByIdentifier(true), listType));
        return messageResourceWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody MessageResourceDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(messageResourceRepository.findByIdentifier(recordId), MessageResourceDto.class);
    }

    @GetMapping("/add")
    @ResponseBody
    public MessageResourceWsDto addMessage() {
        MessageResourceWsDto messageResourceWsDto = new MessageResourceWsDto();
        messageResourceWsDto.setBaseUrl(ADMIN_MESSAGES);
        return messageResourceWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public MessageResourceWsDto editMultiple(@RequestBody MessageResourceWsDto request) {
        MessageResourceWsDto messageResourceWsDto = new MessageResourceWsDto();
        List<MessageResource> messageResources = new ArrayList<>();
        for (MessageResourceDto messageResourceDto : request.getMessageResources()) {
            messageResources.add(messageResourceRepository.findByIdentifier(messageResourceDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<MessageResourceDto>>() {
        }.getType();
        messageResourceWsDto.setMessageResources(modelMapper.map(messageResources, listType));
        messageResourceWsDto.setBaseUrl(ADMIN_MESSAGES);
        return messageResourceWsDto;
    }


    @PostMapping("/edit")
    @ResponseBody
    public MessageResourceWsDto saveMessage(@RequestBody MessageResourceWsDto request) {
        return messageResourceService.handleEdit(request);
    }

    @PostMapping("/delete")
    @ResponseBody
    public MessageResourceWsDto deleteMessage(@RequestBody MessageResourceWsDto messageResourceWsDto) {
        for (MessageResourceDto messageResourceDto : messageResourceWsDto.getMessageResources()) {
            messageResourceService.deleteByIdentifier(messageResourceDto.getIdentifier());
        }
        messageResourceWsDto.setBaseUrl(ADMIN_MESSAGES);
        messageResourceWsDto.setMessage("Data was deleted successfully!!");
        return messageResourceWsDto;
    }

    @PostMapping("/upload")
    @ResponseBody
    public MessageResourceWsDto uploadFile(@RequestBody MultipartFile file) {
        MessageResourceWsDto messageResourceWsDto = new MessageResourceWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.MESSAGES, EntityConstants.MESSAGES, messageResourceWsDto);
            if (StringUtils.isEmpty(messageResourceWsDto.getMessage())) {
                messageResourceWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return messageResourceWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public MessageResourceWsDto uploadFile(@RequestBody MessageResourceWsDto messageResourceWsDto) {
        try {
            messageResourceWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.MESSAGES, messageResourceWsDto.getHeaderFields()));
            return messageResourceWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
