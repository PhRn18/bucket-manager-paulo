package com.project.bucketmanager.Models;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class ApiUser {
    private String username;
    private String password;
    private List<SimpleGrantedAuthority> authorities;

    public ApiUser(String username, String password, List<SimpleGrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public ApiUser() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<SimpleGrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}
