package com.careerhub.controller;
import com.careerhub.dto.ApiResponse;
import com.careerhub.service.AdminService;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/admin")
public class AdminController {
 private final AdminService service;public AdminController(AdminService s){service=s;}
 @GetMapping({"/stats", "/statistics"})public ApiResponse<Map<String,Object>> stats(){return ApiResponse.ok(service.stats());}
 @GetMapping("/users")public ApiResponse<List<Map<String,Object>>> users(){return ApiResponse.ok(service.userList());}
 @GetMapping("/users/trends")public ApiResponse<List<Map<String,Object>>> trends(){return ApiResponse.ok(service.userTrends());}
 @GetMapping("/popular-roles")public ApiResponse<List<Map<String,Object>>> roles(){return ApiResponse.ok(service.popularRoles());}
 @GetMapping("/applications/status")public ApiResponse<List<Map<String,Object>>> status(){return ApiResponse.ok(service.applicationStatuses());}
 @DeleteMapping("/users/{id}")public ApiResponse<Void> delete(@PathVariable String id){service.deleteUser(id);return ApiResponse.ok("User deleted",null);}
}
