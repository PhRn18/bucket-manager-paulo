package com.project.bucketmanager.Services.Impl;

import com.project.bucketmanager.ExceptionHandler.Exceptions.InvalidBearerTokenException;
import com.project.bucketmanager.Models.LoginRequest;
import com.project.bucketmanager.Services.AuthService;
import com.project.bucketmanager.Services.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * A classe `AuthServiceImpl` é responsável por fornecer serviços de autenticação.
 * Ela implementa a interface `AuthService`.
 */
@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    public AuthServiceImpl(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public String login(LoginRequest loginRequest){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.username(),loginRequest.password());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        String token = tokenService.generateToken(authentication);
        if(token == null){
            throw new InvalidBearerTokenException("Error during token generation - Try again later");
        }
        return token;
    }
}
