package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.NodeDto;
import com.touchmind.core.mongo.dto.NodeWsDto;
import com.touchmind.core.mongo.model.Node;
import com.touchmind.core.mongo.model.Role;
import com.touchmind.core.mongo.model.User;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.NodeRepository;
import com.touchmind.core.mongo.repository.UserRepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.NodeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NodeServiceImpl implements NodeService {

    public static final String ADMIN_INTERFACE = "/admin/interface";

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CoreService coreService;

    @Autowired
    private BaseService baseService;

    @Override
    //@Cacheable(cacheNames = "allNodes")
    public List<NodeDto> getAllNodes() {
        List<NodeDto> allNodes = new ArrayList<>();
        List<Node> nodeList = nodeRepository.findByStatusOrderByDisplayPriority(true).stream().filter(node -> node.getParentNode() == null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(nodeList)) {
            for (Node node : nodeList) {
                NodeDto nodeDto = new NodeDto();
                modelMapper.map(node, nodeDto);
                List<Node> childNodes = nodeRepository.findByParentNode(node);
                if (CollectionUtils.isNotEmpty(childNodes)) {
                    List<Node> childNodeList = childNodes.stream().filter(childNode -> BooleanUtils.isTrue(childNode.getStatus()))
                            .sorted(Comparator.comparing(nodes -> nodes.getDisplayPriority())).collect(Collectors.toList());
                    nodeDto.setChildNodes(modelMapper.map(childNodeList, List.class));
                }
                allNodes.add(nodeDto);
            }
        }
        return allNodes;
    }

    @Override
    //@Cacheable(cacheNames = "roleBasedNodes")
    public List<NodeDto> getNodesForRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        User currentUser = userRepository.findByUsername(principalObject.getUsername());
        Set<Role> roles = currentUser.getRoles();
        Set<Node> nodes = new HashSet<>();
        for (Role role : roles) {
            nodes.addAll(role.getPermissions());
        }
        List<NodeDto> allNodes = new ArrayList<>();
        Set<Node> nodeList = nodes.stream().filter(node -> BooleanUtils.isTrue(node.getStatus())).collect(Collectors.toSet());
        List<Node> nodeListArray = new ArrayList<>(nodeList);
        nodeListArray.sort(Comparator.comparing(node -> node.getDisplayPriority(),
                Comparator.nullsLast(Comparator.naturalOrder())));
        Map<String, Set<Node>> parentChildNodes = new HashMap<>();
        for (Node node : nodeListArray) {
            String parent = node.getParentNode().getIdentifier();
            Set<Node> childNodes = new HashSet<>();
            if (parentChildNodes.containsKey(parent)) {
                childNodes.addAll(parentChildNodes.get(parent));
            }
            childNodes.add(node);
            parentChildNodes.put(parent, childNodes);
        }

        for (String key : parentChildNodes.keySet()) {
            NodeDto nodeDto = new NodeDto();
            nodeDto.setIdentifier(key);
            nodeDto.setChildNodes(modelMapper.map(parentChildNodes.get(key), List.class));
            allNodes.add(nodeDto);
        }
        return allNodes;
    }



    @Override
    public NodeWsDto handleEdit(@RequestBody NodeWsDto request) {
        NodeWsDto nodeWsDto = new NodeWsDto();
        List<Node> nodes = new ArrayList<>();
        for (com.touchmind.core.mongo.dto.NodeDto nodeDto : request.getNodes()) {
            Node node = null;
            if (nodeDto.getRecordId() != null) {
                node = nodeRepository.findByRecordId(nodeDto.getRecordId());
                modelMapper.map(nodeDto, node);
            } else {
                if (baseService.validateIdentifier(EntityConstants.NODE, nodeDto.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                node = modelMapper.map(nodeDto, Node.class);
            }
            Node parentNode = node.getParentNode();
            if (parentNode != null) {
                if (parentNode.getRecordId() != null) {
                    node.setParentNode(nodeRepository.findByRecordId(parentNode.getRecordId()));
                }
            }
            baseService.populateCommonData(node);
            nodeRepository.save(node);
            if (nodeDto.getRecordId() == null) {
                node.setRecordId(String.valueOf(node.getId().getTimestamp()));
            }
            nodeRepository.save(node);
            nodes.add(node);
        }
        nodeWsDto.setNodes(modelMapper.map(nodes, List.class));
        nodeWsDto.setMessage("Nodes are updated successfully!!");
        nodeWsDto.setBaseUrl(ADMIN_INTERFACE);
        return nodeWsDto;
    }

    @Override
    public Node findById(String id) {
        //TODO check if this fetch the record correctly
        return nodeRepository.findByRecordId(id);
    }
}