package com.project.bucketmanager.Const;

import com.project.bucketmanager.Models.ApiUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public class ApiUsers {
    private static final List<ApiUser> apiUsers;

    static {
        SimpleGrantedAuthority write = new SimpleGrantedAuthority("ROLE_WRITE");
        SimpleGrantedAuthority read = new SimpleGrantedAuthority("ROLE_READ");
        apiUsers = List.of(
                new ApiUser("read","$2a$10$dZ8GAWXGDQDaPokUDhun.ejxastt4Xz0M0fbDwBQcKu0njI4CLLji",List.of(read)),
                new ApiUser("readwrite","readwrite123",List.of(read, write)));
    }

    public static ApiUser findUserByUsername(String username){
        for(ApiUser apiUser:apiUsers){
            boolean matchName = username.equals(apiUser.getUsername());
            if(matchName){
                return apiUser;
            }
        }
        throw new UsernameNotFoundException("Username not found");
    }

}
