package com.project.bucketmanager.ExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class CustomAccessDeniedHandlerTest {
    private CustomAccessDeniedHandler accessDeniedHandler;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        accessDeniedHandler = new CustomAccessDeniedHandler();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testHandle() throws IOException {
        AccessDeniedException accessDeniedException = new AccessDeniedException("Access Denied");
        accessDeniedHandler.handle(request, response, accessDeniedException);

        assertThat(response.getContentType()).isEqualTo("application/json");
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();


        Map<String, String> responseMap = objectMapper.readValue(responseContent, HashMap.class);
        assertThat(responseMap.get("Error")).isEqualTo("NOT AUTHORIZED TO PERFORM THIS ACTION");
    }
}