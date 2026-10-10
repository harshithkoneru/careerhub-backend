package com.careerhub.controller;
import com.careerhub.dto.*;
import com.careerhub.dto.AuthDtos.UserView;
import com.careerhub.dto.ProfileDtos.UpdateRequest;
import com.careerhub.service.*;
import com.careerhub.util.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping({"/api/profile", "/api/users/profile"})
public class ProfileController {
 private final ProfileService service;public ProfileController(ProfileService s){service=s;}
 @GetMapping public ApiResponse<UserView> get(){return ApiResponse.ok(AuthService.view(service.get(CurrentUser.id())));}
 @PutMapping public ApiResponse<UserView> update(@Valid @RequestBody UpdateRequest r){return ApiResponse.ok("Profile updated",AuthService.view(service.update(CurrentUser.id(),r)));}
}
