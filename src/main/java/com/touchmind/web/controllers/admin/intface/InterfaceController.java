package com.touchmind.web.controllers.admin.intface;

import com.touchmind.core.mongo.dto.NodeDto;
import com.touchmind.core.mongo.dto.NodeWsDto;
import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.model.Node;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.NodeRepository;
import com.touchmind.core.service.NodeService;
import com.touchmind.core.service.UserService;
import com.touchmind.fileimport.service.FileExportService;
import com.touchmind.fileimport.service.FileImportService;
import com.touchmind.fileimport.strategies.EntityType;
import com.touchmind.web.controllers.BaseController;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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

    @PostMapping
    @ResponseBody
    public NodeWsDto getAllModels(@RequestBody NodeWsDto nodeWsDto) {
        Pageable pageable = getPageable(nodeWsDto.getPage(), nodeWsDto.getSizePerPage(), nodeWsDto.getSortDirection(), nodeWsDto.getSortField());
        NodeDto nodeDto = CollectionUtils.isNotEmpty(nodeWsDto.getNodes()) ? nodeWsDto.getNodes().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(nodeDto, nodeWsDto.getOperator());
        Node node = nodeDto != null ? modelMapper.map(nodeDto, Node.class) : null;
        Page<Node> page = isSearchActive(node) != null ? nodeRepository.findAll(Example.of(node, exampleMatcher), pageable) : nodeRepository.findAll(pageable);
        nodeWsDto.setNodes(modelMapper.map(page.getContent(), List.class));
        nodeWsDto.setBaseUrl(ADMIN_INTERFACE);
        nodeWsDto.setTotalPages(page.getTotalPages());
        nodeWsDto.setTotalRecords(page.getTotalElements());
        nodeWsDto.setAttributeList(getConfiguredAttributes(nodeWsDto.getNode()));
        return nodeWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Node());
    }

    @GetMapping("/get")
    @ResponseBody
    public NodeWsDto getActiveNodes() {
        NodeWsDto nodeWsDto = new NodeWsDto();
        nodeWsDto.setBaseUrl(ADMIN_INTERFACE);
        nodeWsDto.setNodes(modelMapper.map(nodeRepository.findByStatusOrderByDisplayPriority(true), List.class));
        return nodeWsDto;
    }

    @GetMapping("/getMenu")
    @ResponseBody
    public List<NodeDto> getMenu() {
        return userService.isAdminRole() ? nodeService.getAllNodes() : nodeService.getNodesForRoles();
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
        return modelMapper.map(nodes, List.class);
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
        nodeWsDto.setNodes(modelMapper.map(nodeRepository.findByStatusOrderByDisplayPriority(true), List.class));
        nodeWsDto.setBaseUrl(ADMIN_INTERFACE);
        return nodeWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public NodeWsDto deleteInterface(@RequestBody NodeWsDto nodeWsDto) {
        for (NodeDto nodeDto : nodeWsDto.getNodes()) {
            nodeRepository.deleteByRecordId(nodeDto.getRecordId());
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
            nodes.add(nodeRepository.findByRecordId(nodeDto.getRecordId()));
        }
        nodeWsDto.setNodes(modelMapper.map(nodes, List.class));
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

    @GetMapping("/export")
    @ResponseBody
    public NodeWsDto uploadFile() {
        NodeWsDto nodeWsDto = new NodeWsDto();
        try {
            nodeWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.NODE));
            return nodeWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
