package com.project.bucketmanager.Models;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class ApiUserTest {
    @Test
    public void testGettersAndSetters() {
        String username = "john";
        String password = "password";
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        ApiUser apiUser = new ApiUser();
        apiUser.setUsername(username);
        apiUser.setPassword(password);
        apiUser.setAuthorities(authorities);

        assertThat(apiUser.getUsername()).isEqualTo(username);
        assertThat(apiUser.getPassword()).isEqualTo(password);
        assertThat(apiUser.getAuthorities()).isEqualTo(authorities);
    }

    @Test
    public void testConstructorWithParameters() {
        String username = "john";
        String password = "password";
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        ApiUser apiUser = new ApiUser(username, password, authorities);

        assertThat(apiUser.getUsername()).isEqualTo(username);
        assertThat(apiUser.getPassword()).isEqualTo(password);
        assertThat(apiUser.getAuthorities()).isEqualTo(authorities);
    }
}