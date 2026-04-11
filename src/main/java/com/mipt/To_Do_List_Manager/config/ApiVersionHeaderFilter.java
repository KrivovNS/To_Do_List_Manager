package com.mipt.To_Do_List_Manager.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiVersionHeaderFilter extends OncePerRequestFilter {

    private final String apiVersion;

    public ApiVersionHeaderFilter(@Value("${app.api.version:2.0.0}") String apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        response.setHeader("X-API-Version", apiVersion);
        filterChain.doFilter(request, response);
    }
}
