package com.touchmind.web.controllers;

import com.touchmind.core.mongo.model.Node;
import com.touchmind.core.mongo.model.Role;
import com.touchmind.core.mongo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AccessDecisionProcessor implements AccessDecisionVoter<FilterInvocation> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public int vote(Authentication authentication, FilterInvocation object, Collection<ConfigAttribute> attributes) {
        assert authentication != null;
        assert object != null;

        //Get the current request URI
        String requestUrl = object.getRequestUrl();
/*
        if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            User currentUser = userRepository.findByUsername(principalObject.getUsername());
            Set<Role> roles = currentUser.getRoles();
            Set<Node> nodes = getNodesForRoles(roles);
            if (!nodes.isEmpty()) {
                if (nodes.stream().filter(node -> node.getPath().contains(requestUrl)).findAny().isPresent()) {
                    return ACCESS_GRANTED;
                } else {
                    return ACCESS_DENIED;
                }
            }
        }

 */
        return ACCESS_GRANTED;
    }

    public Set<Node> getNodesForRoles(Set<Role> roles) {
        Set<Node> nodes = new HashSet<>();
        for (Role role : roles) {
            nodes.addAll(role.getPermissions());
        }
        return nodes;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
