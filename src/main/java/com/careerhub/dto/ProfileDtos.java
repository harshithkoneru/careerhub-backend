package com.careerhub.dto;
import jakarta.validation.constraints.*;
public final class ProfileDtos {
 private ProfileDtos() {}
 public record UpdateRequest(@NotBlank @Size(max=120) String fullName,@Email @Size(max=190) String email,
  @Size(max=40) String phone,@Size(max=160) String college,@Size(max=160) String degree,Integer graduationYear,
  @Size(max=1500) String skills,@Size(max=500) String linkedIn,@Size(max=500) String github,@Size(max=500) String portfolio) {}
}
