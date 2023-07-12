package com.project.bucketmanager.Services.Impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
class TokenServiceImplTest {
    private static TokenServiceImpl tokenService;
    @BeforeAll
    public static void setup() {
        tokenService = new TokenServiceImpl();
    }
    @Test
    public void testGenerateToken() {
        String username = "john";
        String password = "123";
        String authority1 = "ROLE_USER";
        String authority2 = "ROLE_ADMIN";

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(authority1),new SimpleGrantedAuthority(authority2)
        );

        Authentication authentication1 = new UsernamePasswordAuthenticationToken(username,password, authorities);

        String token = tokenService.generateToken(authentication1);

        assertThat(token).isNotNull();
    }
    @Test
    public void testValidateToken() {
        String subject = "john";
        Instant expiration = LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));

        Algorithm algorithm = Algorithm.HMAC256("secret");
        String signedToken = JWT.create()
                .withIssuer("auth-api")
                .withSubject(subject)
                .withExpiresAt(Date.from(expiration))
                .sign(algorithm);

        String verifiedSubject = tokenService.validateToken(signedToken);

        assertThat(verifiedSubject).isEqualTo(subject);
    }
    @Test
    public void testValidateExpiredToken() {
        String subject = "john";
        Instant expiration = LocalDateTime
                .now()
                .minusHours(1)
                .toInstant(ZoneOffset.of("-00:00"));

        Algorithm algorithm = Algorithm.HMAC256("secret");
        String signedToken = JWT.create()
                .withIssuer("auth-api")
                .withSubject(subject)
                .withExpiresAt(Date.from(expiration))
                .sign(algorithm);

        String verifiedSubject = tokenService.validateToken(signedToken);

        assertThat(verifiedSubject).isNull();
    }
}