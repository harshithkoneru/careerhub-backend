package com.careerhub.controller;
import com.careerhub.dto.*;
import com.careerhub.dto.ResumeDtos.View;
import com.careerhub.service.ResumeService;
import com.careerhub.util.CurrentUser;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.util.List;
@RestController @RequestMapping({"/api/resumes", "/api/resume"})
public class ResumeController {
 private final ResumeService service;public ResumeController(ResumeService s){service=s;}
 @GetMapping public ApiResponse<List<View>> list(){return ApiResponse.ok(service.list(CurrentUser.id()));}
 @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE)public ApiResponse<View> upload(@RequestPart("file")MultipartFile file){return ApiResponse.ok("Resume uploaded",service.upload(CurrentUser.id(),file));}
 @PutMapping("/{id}/default")public ApiResponse<View> makeDefault(@PathVariable String id){return ApiResponse.ok(service.makeDefault(CurrentUser.id(),id));}
 @GetMapping("/{id}/download")public ResponseEntity<Resource> download(@PathVariable String id){
   Path p=service.file(CurrentUser.id(),id);Resource resource=new FileSystemResource(p);
   return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+p.getFileName()+"\"").body(resource);
 }
 @DeleteMapping("/{id}")public ApiResponse<Void> delete(@PathVariable String id){service.delete(CurrentUser.id(),id);return ApiResponse.ok("Resume deleted",null);}
}
