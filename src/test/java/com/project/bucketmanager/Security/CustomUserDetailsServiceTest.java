package com.project.bucketmanager.Security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
@SpringBootTest
class CustomUserDetailsServiceTest {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Test
    void loadUserByUsername_ValidUsername() {
        String username = "read";
        UserDetails result = customUserDetailsService.loadUserByUsername(username);
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(username);
    }
    @Test
    void loadUserByUsername_InvalidUsername(){
        String invalidUsername = "read123";
        assertThatThrownBy(()->customUserDetailsService.loadUserByUsername(invalidUsername))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}