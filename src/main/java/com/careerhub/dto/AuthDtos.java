package com.careerhub.dto;

import jakarta.validation.constraints.*;
import com.careerhub.entity.Role;

public final class AuthDtos {
    private AuthDtos() {}
    public record RegisterRequest(@NotBlank @Size(max=120) String fullName,@NotBlank @Email @Size(max=190) String email,
       @NotBlank @Size(min=8,max=72) String password,@Size(max=40) String phone,@Size(max=160) String college,
       @Size(max=160) String degree,Integer graduationYear) {}
    public record LoginRequest(@NotBlank @Email String email,@NotBlank String password) {}
    public record UserView(String id,String fullName,String email,Role role,String phone,String college,String degree,
       Integer graduationYear,String skills,String linkedIn,String github,String portfolio) {}
    public record AuthResponse(String token,String tokenType,UserView user) {}
}
