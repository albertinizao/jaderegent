package com.opipo.jaderegent.infrastructure.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SpaForwardFilterTest {

    private SpaForwardFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        filter = new SpaForwardFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        dispatcher = mock(RequestDispatcher.class);
        when(request.getContextPath()).thenReturn("");
        when(request.getRequestDispatcher("/index.html")).thenReturn(dispatcher);
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Accept")).thenReturn("text/html");
    }

    @Test
    void forwardsHtmlDeepLinkToIndex() throws Exception {
        when(request.getRequestURI()).thenReturn("/npcs/42");

        filter.doFilter(request, response, chain);

        verify(dispatcher).forward(request, response);
        verifyNoInteractions(chain);
    }

    @Test
    void doesNotForwardApiRequests() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/npc");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(any(ServletRequest.class), any(ServletResponse.class));
        verifyNoInteractions(dispatcher);
    }

    @Test
    void doesNotForwardAssetsOrDottedPaths() throws Exception {
        when(request.getRequestURI()).thenReturn("/assets/index-abc.js");
        filter.doFilter(request, response, chain);
        when(request.getRequestURI()).thenReturn("/favicon.ico");
        filter.doFilter(request, response, chain);

        verify(chain, times(2)).doFilter(any(ServletRequest.class), any(ServletResponse.class));
        verifyNoInteractions(dispatcher);
    }

    @Test
    void doesNotForwardNonGetRequests() throws Exception {
        when(request.getRequestURI()).thenReturn("/npcs");
        when(request.getMethod()).thenReturn("POST");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(any(ServletRequest.class), any(ServletResponse.class));
        verifyNoInteractions(dispatcher);
    }
}
