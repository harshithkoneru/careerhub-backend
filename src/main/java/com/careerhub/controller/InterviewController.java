package com.careerhub.controller;
import com.careerhub.dto.*;
import com.careerhub.dto.InterviewDtos.*;
import com.careerhub.service.InterviewService;
import com.careerhub.util.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/interviews")
public class InterviewController {
 private final InterviewService service;public InterviewController(InterviewService s){service=s;}
 @GetMapping public ApiResponse<List<View>> list(@RequestParam(required=false)String filter){return ApiResponse.ok(service.list(CurrentUser.id(),filter));}
 @GetMapping("/upcoming")public ApiResponse<List<View>> upcoming(){return ApiResponse.ok(service.list(CurrentUser.id(),"upcoming"));}
 @GetMapping("/completed")public ApiResponse<List<View>> completed(){return ApiResponse.ok(service.list(CurrentUser.id(),"completed"));}
 @GetMapping("/{id}")public ApiResponse<View> get(@PathVariable String id){return ApiResponse.ok(service.get(CurrentUser.id(),id));}
 @PostMapping public ApiResponse<View> create(@Valid @RequestBody SaveRequest r){return ApiResponse.ok("Interview created",service.create(CurrentUser.id(),r));}
 @PutMapping("/{id}")public ApiResponse<View> update(@PathVariable String id,@Valid @RequestBody SaveRequest r){return ApiResponse.ok("Interview updated",service.update(CurrentUser.id(),id,r));}
 @DeleteMapping("/{id}")public ApiResponse<Void> delete(@PathVariable String id){service.delete(CurrentUser.id(),id);return ApiResponse.ok("Interview deleted",null);}
}
