package com.project.bucketmanager.Models;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LoginRequestTest {
    @Test
    public void testRecordFunctionality() {
        String username = "john";
        String password = "password";

        LoginRequest loginRequest = new LoginRequest(username, password);

        assertThat(loginRequest.username()).isEqualTo(username);
        assertThat(loginRequest.password()).isEqualTo(password);

        LoginRequest sameLoginRequest = new LoginRequest(username, password);
        assertThat(loginRequest).isEqualTo(sameLoginRequest);
        assertThat(loginRequest.hashCode()).isEqualTo(sameLoginRequest.hashCode());

        String expectedToString = "LoginRequest[username=" + username + ", password=" + password + "]";
        assertThat(loginRequest.toString()).isEqualTo(expectedToString);
    }
}