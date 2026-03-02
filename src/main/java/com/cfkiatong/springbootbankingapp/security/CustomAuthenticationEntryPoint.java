package com.cfkiatong.springbootbankingapp.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        String message;
        if (authException instanceof DisabledException) {
            message = "User account is disabled.";
        } else if (authException instanceof LockedException) {
            message = "User account is locked.";
        } else {
            message = "Authentication failed.";
        }
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }

}