package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.NodeDto;
import com.touchMind.core.mongo.dto.NodeWsDto;
import com.touchMind.core.mongo.model.Node;
import com.touchMind.core.mongo.model.Role;
import com.touchMind.core.mongo.model.User;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.NodeRepository;
import com.touchMind.core.mongo.repository.UserRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.NodeService;
import com.google.common.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Type;
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
                    Type listType = new TypeToken<List<NodeDto>>() {
                    }.getType();
                    nodeDto.setChildNodes(modelMapper.map(childNodeList, listType));
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
            if (CollectionUtils.isNotEmpty(role.getPermissions())) {
                nodes.addAll(role.getPermissions());
            }
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
            Type listType = new TypeToken<List<NodeDto>>() {
            }.getType();
            nodeDto.setChildNodes(modelMapper.map(parentChildNodes.get(key), listType));
            allNodes.add(nodeDto);
        }
        return allNodes;
    }

    @Override
    public NodeWsDto handleEdit(@RequestBody NodeWsDto request) {
        NodeWsDto nodeWsDto = new NodeWsDto();
        List<Node> nodes = new ArrayList<>();
        for (com.touchMind.core.mongo.dto.NodeDto nodeDto : request.getNodes()) {
            if (nodeDto.isAdd() && baseService.validateIdentifier(EntityConstants.NODE, nodeDto.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            Node node = nodeRepository.findByIdentifier(nodeDto.getIdentifier());
            if (node != null) {
                modelMapper.map(nodeDto, node);
            } else {
                node = modelMapper.map(nodeDto, Node.class);
            }
            Node parentNode = node.getParentNode();
            if (parentNode != null) {
                if (parentNode.getIdentifier() != null) {
                    node.setParentNode(nodeRepository.findByIdentifier(parentNode.getIdentifier()));
                }
            }
            baseService.populateCommonData(node);
            if (node.getDisplayPriority() == null) {
                node.setDisplayPriority(1000);
            }
            nodeRepository.save(node);
            nodes.add(node);
        }
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        nodeWsDto.setNodes(modelMapper.map(nodes, listType));
        nodeWsDto.setMessage("Nodes updated successfully!!");
        nodeWsDto.setBaseUrl(ADMIN_INTERFACE);
        return nodeWsDto;
    }

    @Override
    public Node findById(String id) {
        //TODO check if this fetch the record correctly
        return nodeRepository.findByIdentifier(id);
    }
}
