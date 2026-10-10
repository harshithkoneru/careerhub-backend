package com.careerhub.controller;
import com.careerhub.dto.*;
import com.careerhub.dto.ApplicationDtos.*;
import com.careerhub.service.ApplicationService;
import com.careerhub.util.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/applications")
public class ApplicationController {
 private final ApplicationService service;public ApplicationController(ApplicationService s){service=s;}
 @GetMapping public ApiResponse<Map<String,Object>> list(@RequestParam(required=false)String status,
   @RequestParam(required=false)String q,@RequestParam(required=false)String search,@RequestParam(required=false)String jobRole,
   @RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="100")int size,@RequestParam(defaultValue="applicationDate")String sort,
   @RequestParam(defaultValue="desc")String direction){
   String searchTerm=(search==null||search.isBlank())?q:search;
   Page<View> result=service.list(CurrentUser.id(),status,searchTerm,jobRole,page,size,sort,direction);
   return ApiResponse.ok(Map.of("items",result.getContent(),"page",result.getNumber(),"size",result.getSize(),"totalElements",result.getTotalElements(),"totalPages",result.getTotalPages()));
 }
 @GetMapping("/{id}")public ApiResponse<View> get(@PathVariable String id){return ApiResponse.ok(service.get(CurrentUser.id(),id));}
 @PostMapping public ApiResponse<View> create(@Valid @RequestBody SaveRequest r){return ApiResponse.ok("Application created",service.create(CurrentUser.id(),r));}
 @PutMapping("/{id}")public ApiResponse<View> update(@PathVariable String id,@Valid @RequestBody SaveRequest r){return ApiResponse.ok("Application updated",service.update(CurrentUser.id(),id,r));}
 @DeleteMapping("/{id}")public ApiResponse<Void> delete(@PathVariable String id){service.delete(CurrentUser.id(),id);return ApiResponse.ok("Application deleted",null);}
}
