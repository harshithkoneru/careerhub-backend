package com.careerhub.controller;
import com.careerhub.dto.ApiResponse;
import com.careerhub.service.CareerAnalysisService;
import com.careerhub.util.CurrentUser;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/career")
public class CareerController {
 private final CareerAnalysisService service;public CareerController(CareerAnalysisService s){service=s;}
 @GetMapping("/analysis")public ApiResponse<Map<String,Object>> analysis(){return ApiResponse.ok(service.analysis(CurrentUser.id()));}
 @GetMapping("/role-performance")public ApiResponse<List<Map<String,Object>>> roles(){return ApiResponse.ok(service.rolePerformance(CurrentUser.id()));}
 @GetMapping("/rejection-analysis")public ApiResponse<List<Map<String,Object>>> rejection(){return ApiResponse.ok(service.rejectionAnalysis(CurrentUser.id()));}
 @GetMapping("/recommendations")public ApiResponse<Object> recommendations(){return ApiResponse.ok(service.analysis(CurrentUser.id()).get("recommendations"));}
 @GetMapping({"/health", "/health-score"})public ApiResponse<Object> health(){return ApiResponse.ok(service.analysis(CurrentUser.id()).get("careerHealth"));}
}
