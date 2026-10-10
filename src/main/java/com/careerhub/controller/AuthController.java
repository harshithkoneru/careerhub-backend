package com.careerhub.controller;
import com.careerhub.dto.*;
import com.careerhub.dto.AuthDtos.*;
import com.careerhub.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final AuthService service;public AuthController(AuthService service){this.service=service;}
 @PostMapping("/register") public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest r){return ApiResponse.ok("Account created",service.register(r));}
 @PostMapping("/login") public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest r){return ApiResponse.ok(service.login(r));}
}
