package com.project.bucketmanager.Services.Impl;

import com.project.bucketmanager.ExceptionHandler.Exceptions.InvalidBearerTokenException;
import com.project.bucketmanager.Models.LoginRequest;
import com.project.bucketmanager.Services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenService tokenService;
    private AuthServiceImpl authService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(authenticationManager,tokenService);
    }
    @Test
    void testLoginWithValidCredentials() {
        String username = "testUser";
        String password = "testPassword";
        LoginRequest loginRequest = new LoginRequest(username, password);
        String token = "testToken";

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(authenticationToken)).thenReturn(authentication);
        when(tokenService.generateToken(authentication)).thenReturn(token);

        String result = authService.login(loginRequest);

        assertThat(result).isEqualTo(token);
        verify(authenticationManager).authenticate(authenticationToken);
        verify(tokenService).generateToken(authentication);
    }

    @Test
    void testLoginWithInvalidCredentials() {
        String username = "testUser";
        String password = "testPassword";
        LoginRequest loginRequest = new LoginRequest(username, password);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        when(tokenService.generateToken(authenticationToken)).thenThrow(InvalidBearerTokenException.class);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidBearerTokenException.class)
                .hasMessage("Error during token generation - Try again later");

        verify(authenticationManager).authenticate(authenticationToken);
    }
}