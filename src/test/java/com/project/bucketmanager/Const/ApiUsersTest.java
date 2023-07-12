package com.project.bucketmanager.Const;


import com.project.bucketmanager.Models.ApiUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.*;

class ApiUsersTest {
    @Test
    void findReaderUser(){
        String username = "read";
        ApiUser apiUser = ApiUsers.findUserByUsername(username);
        assertThat(apiUser).isNotNull();
        assertThat(apiUser.getAuthorities().size()).isNotZero();
        assertThat(apiUser.getUsername()).isEqualTo(username);
    }
    @Test
    void findReaderAndWritterUser(){
        String username = "readwrite";
        ApiUser apiUser = ApiUsers.findUserByUsername(username);
        assertThat(apiUser).isNotNull();
        assertThat(apiUser.getUsername()).isEqualTo(username);
        assertThat(apiUser.getAuthorities().size()).isNotZero();
    }
    @Test
    void userNotFound(){
        String invalidUsername = "asdsdasda";
        assertThatThrownBy(()-> ApiUsers.findUserByUsername(invalidUsername))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}