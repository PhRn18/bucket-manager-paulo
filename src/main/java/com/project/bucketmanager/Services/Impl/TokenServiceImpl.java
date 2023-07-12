package com.project.bucketmanager.Services.Impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.bucketmanager.ExceptionHandler.Exceptions.InvalidBearerTokenException;
import com.project.bucketmanager.Services.TokenService;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * A classe `TokenServiceImpl` é responsável por gerar e validar tokens de autenticação usando a biblioteca JWT (JSON Web Token).
 * Ela implementa a interface `TokenService`.
 */
@Service
public class TokenServiceImpl implements TokenService {
    private final String secret = "secret";
    @Nullable
    public String generateToken(Authentication authentication){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            return JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(authentication.getName())
                    .withClaim("roles", roles)
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            return null;
        }
    }
    @Nullable
    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            DecodedJWT jwt = JWT.decode(token);
            Date expiration = jwt.getExpiresAt();
            Date now = new Date();

            if (expiration == null || expiration.before(now)) {
                return null;
            }

            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException | InvalidBearerTokenException exception){
            return null;
        }
    }

    /**
     * Gera a data de expiração para o token.
     *
     * @return A data de expiração, configurada para 2 horas a partir do momento atual.
     */
    private Instant genExpirationDate(){
        return LocalDateTime.now().plusSeconds(10).toInstant(ZoneOffset.of("-03:00"));
    }

}
