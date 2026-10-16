package com.careerhub.controller;
import com.careerhub.dto.*;
import com.careerhub.dto.AuthDtos.*;
import com.careerhub.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final AuthService service;public AuthController(AuthService service){this.service=service;}
 @PostMapping("/register") public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest r){
   return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Account created",service.register(r)));
 }
 @PostMapping("/login") public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest r){return ApiResponse.ok(service.login(r));}
}
