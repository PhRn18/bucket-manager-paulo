package com.project.bucketmanager.Services;

import org.springframework.security.core.Authentication;

public interface TokenService {
    /**
     * Gera um token de autenticação com base nas informações fornecidas pela autenticação.
     *
     * @param authentication A autenticação contendo as informações do usuário autenticado.
     * @return O token de autenticação gerado.
     * @throws IllegalArgumentException Caso ocorra um erro durante a geração do token.
     */
    String generateToken(Authentication authentication);
    /**
     * Valida um token de autenticação.
     *
     * @param token O token de autenticação a ser validado.
     * @return O nome de usuário associado ao token, se o token for válido e não estiver expirado. Caso contrário, retorna `null`.
     */
    String validateToken(String token);

}
