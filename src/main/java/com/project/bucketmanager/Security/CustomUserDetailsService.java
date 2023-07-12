package com.project.bucketmanager.Security;
import com.project.bucketmanager.Const.ApiUsers;
import com.project.bucketmanager.Models.ApiUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        ApiUser apiUser = ApiUsers.findUserByUsername(s);
        return new User(apiUser.getUsername(), apiUser.getPassword(), apiUser.getAuthorities());
    }
}
