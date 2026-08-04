package com.opipo.jaderegent.infrastructure.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Forwards browser navigations to the React entry point while leaving API and
 * static-resource requests to their normal Spring handlers.
 */
@Component
public class SpaForwardFilter extends OncePerRequestFilter {

    private static final String INDEX = "/index.html";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (shouldForward(request)) {
            request.getRequestDispatcher(INDEX).forward(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean shouldForward(HttpServletRequest request) {
        if (!HttpMethod.GET.matches(request.getMethod())) {
            return false;
        }

        String accept = request.getHeader("Accept");
        if (accept == null || !accept.toLowerCase().contains("text/html")) {
            return false;
        }

        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (path.isEmpty()) {
            path = "/";
        }

        if (isExcludedPath(path)) {
            return false;
        }

        // A path whose final segment contains a dot is a file request (for
        // example favicon.ico or an asset), not a client-side route.
        int lastSlash = path.lastIndexOf('/');
        return !path.substring(lastSlash + 1).contains(".");
    }

    private boolean isExcludedPath(String path) {
        return path.equals("/api") || path.startsWith("/api/")
                || path.equals("/data") || path.startsWith("/data/")
                || path.equals("/assets") || path.startsWith("/assets/")
                || path.equals("/actuator") || path.startsWith("/actuator/");
    }
}
