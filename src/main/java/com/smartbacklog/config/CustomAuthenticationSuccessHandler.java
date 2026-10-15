package com.smartbacklog.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if ("ROLE_ADMIN".equals(role)) {
                response.sendRedirect("/admin/dashboard");
                return;
            } else if ("ROLE_HOD".equals(role)) {
                response.sendRedirect("/hod/dashboard");
                return;
            } else if ("ROLE_STUDENT".equals(role)) {
                response.sendRedirect("/student/dashboard");
                return;
            } else if ("ROLE_PARENT".equals(role)) {
                response.sendRedirect("/parent/dashboard");
                return;
            } else if ("ROLE_TUTOR".equals(role)) {
                response.sendRedirect("/tutor/dashboard");
                return;
            }
        }
        response.sendRedirect("/login");
    }
}
