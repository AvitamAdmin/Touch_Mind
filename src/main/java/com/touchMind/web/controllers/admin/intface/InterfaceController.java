package com.touchMind.web.controllers.admin.intface;

import com.touchMind.core.mongo.dto.NodeDto;
import com.touchMind.core.mongo.dto.NodeWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.Node;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.NodeRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.NodeService;
import com.touchMind.core.service.UserService;
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

@RestController
@RequestMapping("/admin/interface")
public class InterfaceController extends BaseController {

    public static final String ADMIN_INTERFACE = "/admin/interface";
    Logger logger = LoggerFactory.getLogger(InterfaceController.class);
    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;

    @PostMapping
    @ResponseBody
    public NodeWsDto getAllModels(@RequestBody NodeWsDto nodeWsDto) {
        Pageable pageable = getPageable(nodeWsDto.getPage(), nodeWsDto.getSizePerPage(), nodeWsDto.getSortDirection(), nodeWsDto.getSortField());
        NodeDto nodeDto = CollectionUtils.isNotEmpty(nodeWsDto.getNodes()) ? nodeWsDto.getNodes().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(nodeDto, nodeWsDto.getOperator());
        Node node = nodeDto != null ? modelMapper.map(nodeDto, Node.class) : null;
        if (node != null) {
            node.setDisplayPriority(nodeDto.getDisplayPriority());
        }
        Page<Node> page = isSearchActive(node) != null ? nodeRepository.findAll(Example.of(node, exampleMatcher), pageable) : nodeRepository.findAll(pageable);
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        nodeWsDto.setNodes(modelMapper.map(page.getContent(), listType));
        nodeWsDto.setBaseUrl(ADMIN_INTERFACE);
        nodeWsDto.setTotalPages(page.getTotalPages());
        nodeWsDto.setTotalRecords(page.getTotalElements());
        nodeWsDto.setAttributeList(getConfiguredAttributes(nodeWsDto.getNode()));
        nodeWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.INTERFACE_CONFIG));
        return nodeWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody NodeWsDto nodeWsDto) {
        return getConfiguredAttributes(nodeWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Node());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.NODE);
    }

    @GetMapping("/get")
    @ResponseBody
    public NodeWsDto getActiveNodes() {
        NodeWsDto nodeWsDto = new NodeWsDto();
        nodeWsDto.setBaseUrl(ADMIN_INTERFACE);
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        nodeWsDto.setNodes(modelMapper.map(nodeRepository.findByStatusOrderByDisplayPriority(true), listType));
        return nodeWsDto;
    }

    @GetMapping("/getParent")
    @ResponseBody
    public NodeWsDto getParentNodes() {
        NodeWsDto nodeWsDto = new NodeWsDto();
        nodeWsDto.setBaseUrl(ADMIN_INTERFACE);
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        nodeWsDto.setNodes(modelMapper.map(nodeRepository.findByParentNodeAndStatus(null, true), listType));
        return nodeWsDto;
    }

    @GetMapping("/getMenu")
    @ResponseBody
    public List<NodeDto> getMenu() {
        return userService.isAdminRole() ? nodeService.getAllNodes() : nodeService.getNodesForRoles();
    }

    @PostMapping("/getMenu/search")
    @ResponseBody
    public List<NodeDto> getMenu(@RequestBody NodeDto nodeDto) {
        List<NodeDto> nodeDtoList = userService.isAdminRole() ? nodeService.getAllNodes() : nodeService.getNodesForRoles();
        List<NodeDto> nodeDtoListFinal = new ArrayList<>();
        for (NodeDto node : nodeDtoList) {
            List<NodeDto> childNodes = node.getChildNodes().stream().filter(nodeDto1 -> StringUtils.containsIgnoreCase(nodeDto1.getIdentifier(), nodeDto.getIdentifier())).toList();
            if (CollectionUtils.isNotEmpty(childNodes)) {
                node.setChildNodes(childNodes);
                nodeDtoListFinal.add(node);
            }
        }
        return nodeDtoListFinal;
    }

    @GetMapping("/getMenu/{id}")
    @ResponseBody
    public List<NodeDto> getAdminMenu(@PathVariable(required = true) String id) {
        List<Node> nodes = new ArrayList<>();
        Node parentNode = nodeRepository.findByIdentifier(id);
        nodes.addAll(nodeRepository.findByParentNode(parentNode));
        for (String identifier : "Data,Role & Users,Process & guides".split(",")) {
            nodes.addAll(nodeRepository.findByParentNode(nodeRepository.findByIdentifier(identifier)));
        }
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        return modelMapper.map(nodes, listType);
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody NodeDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(nodeRepository.findByIdentifier(recordId), NodeDto.class);
    }

    @PostMapping("/edit")
    @ResponseBody
    public NodeWsDto handleEdit(@RequestBody NodeWsDto request) {
        return nodeService.handleEdit(request);
    }

    @GetMapping("/add")
    @ResponseBody
    public NodeWsDto addInterface() {
        NodeWsDto nodeWsDto = new NodeWsDto();
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        nodeWsDto.setNodes(modelMapper.map(nodeRepository.findByStatusOrderByDisplayPriority(true), listType));
        nodeWsDto.setBaseUrl(ADMIN_INTERFACE);
        return nodeWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public NodeWsDto deleteInterface(@RequestBody NodeWsDto nodeWsDto) {
        for (NodeDto nodeDto : nodeWsDto.getNodes()) {
            nodeRepository.deleteByIdentifier(nodeDto.getIdentifier());
        }
        nodeWsDto.setBaseUrl(ADMIN_INTERFACE);
        nodeWsDto.setMessage("Data deleted successfully!!");
        return nodeWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public NodeWsDto editMultiple(@RequestBody NodeWsDto request) {
        NodeWsDto nodeWsDto = new NodeWsDto();
        List<Node> nodes = new ArrayList<>();
        for (NodeDto nodeDto : request.getNodes()) {
            nodes.add(nodeRepository.findByIdentifier(nodeDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        nodeWsDto.setNodes(modelMapper.map(nodes, listType));
        nodeWsDto.setBaseUrl(ADMIN_INTERFACE);
        nodeWsDto.setRedirectUrl("/admin/interface");
        return nodeWsDto;
    }

    @PostMapping("/upload")
    @ResponseBody
    public NodeWsDto uploadFile(@RequestParam("file") MultipartFile file) {
        NodeWsDto nodeWsDto = new NodeWsDto();
        try {
            boolean isSuccess = fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.NODE, EntityConstants.NODE, nodeWsDto);
            if (isSuccess) {
                if (StringUtils.isEmpty(nodeWsDto.getMessage())) {
                    nodeWsDto.setMessage("File uploaded successfully!!");
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return nodeWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public NodeWsDto uploadFile(@RequestBody NodeWsDto nodeWsDto) {

        try {
            nodeWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.NODE, nodeWsDto.getHeaderFields()));
            return nodeWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
