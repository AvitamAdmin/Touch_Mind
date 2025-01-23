package com.touchmind.web.filter;

import com.touchmind.core.SpringContext;
import com.touchmind.core.mongo.model.Role;
import com.touchmind.core.mongo.repository.RoleRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class RequestResponseLoggingFilter implements Filter {

    private RoleRepository roleRepository;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        HttpServletRequest httpServletRequest = ((HttpServletRequest) request);
        String requestHeader = httpServletRequest.getHeader("X-Requested-With");
        if (CollectionUtils.isNotEmpty(authorities) && ("XMLHttpRequest".equals(requestHeader))) {
            for (GrantedAuthority authority : authorities) {
                String roleName = authority.getAuthority();
                if (!roleName.equalsIgnoreCase("ROLE_ANONYMOUS")) {
                    populateQuotaDetails(httpServletRequest, authority, response);
                }
            }
        }
        chain.doFilter(request, response);
    }

    private void populateQuotaDetails(HttpServletRequest httpServletRequest, GrantedAuthority authority, ServletResponse response) {
        roleRepository = SpringContext.getBean(RoleRepository.class);
        Role role = roleRepository.findByIdentifier(authority.getAuthority());
        if (StringUtils.isNotEmpty(role.getQuota())) {
            long quota = Long.parseLong(role.getQuota());
            long quotaUsed = 0;
            if (StringUtils.isNotEmpty(role.getQuotaUsed())) {
                quotaUsed = Long.parseLong(role.getQuotaUsed()) + 1;
            }
            if (quotaUsed > quota) {
                HttpServletResponse servletResponse = (HttpServletResponse) response;
                try {
                    servletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login?quota");
                    HttpSession session = httpServletRequest.getSession(false);
                    session.invalidate();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                role.setQuotaUsed(String.valueOf(quotaUsed));
                roleRepository.save(role);
            }
        }
    }
}
