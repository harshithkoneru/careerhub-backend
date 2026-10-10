package com.careerhub.controller;
import com.careerhub.dto.ApiResponse;
import com.careerhub.service.DashboardService;
import com.careerhub.util.CurrentUser;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping({"/api/dashboard", "/api/dashboard/statistics"})
public class DashboardController {
 private final DashboardService service;public DashboardController(DashboardService s){service=s;}
 @GetMapping public ApiResponse<Map<String,Object>> dashboard(){return ApiResponse.ok(service.data(CurrentUser.id()));}
}
