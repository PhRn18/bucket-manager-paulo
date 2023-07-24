package com.project.bucketmanager.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class HealthyControllerTest {
    private MockMvc mockMvc;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        HealthyController healthyController = new HealthyController();
        mockMvc = MockMvcBuilders.standaloneSetup(healthyController).build();
    }


    @Test
    void checkHealthyStatus() throws Exception {
        mockMvc.perform(
                get("/healthy")
        )
                .andExpect(status().isOk())
                .andReturn();
    }
}