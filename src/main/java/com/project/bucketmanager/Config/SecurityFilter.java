package com.project.bucketmanager.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bucketmanager.Const.ApiUsers;
import com.project.bucketmanager.Models.ApiUser;
import com.project.bucketmanager.Services.TokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;
    public SecurityFilter(TokenService tokenService,ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = retreiveTokenFromHeader(request);
        if(token!=null){
            String login = tokenService.validateToken(token);
            if (login == null) {
                sendErrorMessage(response);
                return;
            }
            ApiUser user = ApiUsers.findUserByUsername(login);
            Authentication authToken = new UsernamePasswordAuthenticationToken(user.getUsername(),token,user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }
    private void sendErrorMessage(HttpServletResponse response) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("Error", "Invalid or expired Bearer token");

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(params));
    }
    private String retreiveTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if(authHeader==null) return null;
        return authHeader.replace("Bearer ","");
    }
}
