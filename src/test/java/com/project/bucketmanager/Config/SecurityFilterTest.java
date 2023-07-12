package com.project.bucketmanager.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bucketmanager.Services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;
@SpringBootTest
class SecurityFilterTest {
    @Mock
    private TokenService tokenService;
    @Mock
    private HttpServletRequest request;

    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    private SecurityFilter securityFilter;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        response = new MockHttpServletResponse();
        securityFilter = new SecurityFilter(tokenService, new ObjectMapper());
    }

    @Test
    public void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        String token = "validToken";
        String login = "read";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.validateToken(token)).thenReturn(login);
        securityFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        String token = "invalidToken";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.validateToken(token)).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_NoToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        securityFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testSendErrorMessage() throws IOException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        when(httpServletResponse.getWriter()).thenReturn(writer);
        securityFilter.sendErrorMessage(httpServletResponse);

        verify(httpServletResponse).setContentType("application/json");

        verify(httpServletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}