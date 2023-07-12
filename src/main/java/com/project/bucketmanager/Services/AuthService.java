package com.project.bucketmanager.Services;

import com.project.bucketmanager.Models.ApiUser;
import com.project.bucketmanager.Models.LoginRequest;
import org.springframework.security.core.Authentication;

public interface AuthService {
    /**
     * Realiza o processo de login, autenticando o usuário com base nas informações fornecidas.
     * Após a autenticação bem-sucedida, um token de autenticação é gerado e retornado.
     *
     * @param loginRequest O objeto contendo as informações de login, como nome de usuário e senha.
     * @return O token de autenticação gerado após a autenticação bem-sucedida.
     */
    String login(LoginRequest loginRequest);
}
