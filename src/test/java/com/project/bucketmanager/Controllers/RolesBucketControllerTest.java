package com.project.bucketmanager.Controllers;

import com.project.bucketmanager.Services.BucketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class RolesBucketControllerTest {
    private MockMvc mockMvc;
    @Mock
    private BucketService bucketService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        BucketController bucketController = new BucketController(bucketService);
        mockMvc = MockMvcBuilders.standaloneSetup(bucketController).build();
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
