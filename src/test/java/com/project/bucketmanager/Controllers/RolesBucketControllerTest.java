package com.project.bucketmanager.Controllers;

import com.project.bucketmanager.Services.BucketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class RolesBucketControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BucketService bucketService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    @WithMockUser(roles = "WRITE")
    public void testDeleteBucketFileWithWriteRole() throws Exception {
        String bucketName = "bucket-1";
        String key = "file.txt";
        doNothing().when(bucketService).deleteFileFromBucket(anyString(),anyString());
        mockMvc.perform(
                        delete("/bucket/{bucketName}", bucketName)
                                .queryParam("key",key)
                )
                .andExpect(status().isCreated())
                .andReturn();

        verify(bucketService).deleteFileFromBucket(bucketName, key);
    }
    //TODO Implement tests depending on the logic of permissions for each user's methods.
}
