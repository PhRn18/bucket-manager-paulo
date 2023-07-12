package com.project.bucketmanager.Controllers;

import com.project.bucketmanager.Models.LoginRequest;
import com.project.bucketmanager.Models.LoginResponse;
import com.project.bucketmanager.Services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        String result = authService.login(loginRequest);
        return ResponseEntity.ok(new LoginResponse(result));
    }
}
