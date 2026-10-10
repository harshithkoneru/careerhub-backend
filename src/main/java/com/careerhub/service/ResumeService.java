package com.careerhub.service;
import com.careerhub.dto.ResumeDtos.View;
import com.careerhub.entity.Resume;
import com.careerhub.exception.ApiException;
import com.careerhub.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class ResumeService {
 private final ResumeRepository resumes;private final UserRepository users;private final Path directory;private final long maxBytes;
 public ResumeService(ResumeRepository r,UserRepository u,@Value("${app.upload.directory}")String path,@Value("${app.upload.max-bytes:5242880}")long maxBytes){
   resumes=r;users=u;directory=Paths.get(path).toAbsolutePath().normalize();this.maxBytes=maxBytes;
 }
 public List<View> list(String uid){return resumes.findAllByUserIdOrderByUploadedAtDesc(uid).stream().map(ResumeService::view).toList();}
 @Transactional public View upload(String uid,MultipartFile file){
   if(file==null||file.isEmpty())throw ApiException.badRequest("A PDF file is required");
   if(file.getSize()>maxBytes)throw ApiException.badRequest("Resume exceeds the maximum allowed size");
   String suppliedName=Objects.requireNonNullElse(file.getOriginalFilename(),"resume.pdf").replace('\\','/');
   String original=suppliedName.substring(suppliedName.lastIndexOf('/')+1);
   String contentType=file.getContentType();
   if(!original.toLowerCase(Locale.ROOT).endsWith(".pdf")||(contentType!=null&&!List.of("application/pdf","application/octet-stream").contains(contentType.toLowerCase(Locale.ROOT))))
     throw ApiException.badRequest("Only PDF resumes are accepted");
   try {
     byte[] header=file.getBytes();
     if(header.length<5||header[0]!='%'||header[1]!='P'||header[2]!='D'||header[3]!='F'||header[4]!='-')throw ApiException.badRequest("The uploaded file is not a valid PDF");
     Files.createDirectories(directory);
     String key=UUID.randomUUID()+".pdf";Path destination=directory.resolve(key).normalize();
     if(!destination.getParent().equals(directory))throw ApiException.badRequest("Invalid filename");
     Files.write(destination,header,StandardOpenOption.CREATE_NEW);
     Resume resume=new Resume();resume.setUser(users.findById(uid).orElseThrow(()->ApiException.notFound("User")));resume.setName(original);resume.setStorageKey(key);resume.setSize(file.getSize());
     return view(resumes.save(resume));
   }catch(IOException e){throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,"Unable to store uploaded resume");}
 }
 @Transactional public void delete(String uid,String id){
   Resume r=owned(uid,id);Path file=directory.resolve(r.getStorageKey()).normalize();
   try{Files.deleteIfExists(file);}catch(IOException e){throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,"Unable to delete resume file");}
   boolean wasDefault=r.isDefault();resumes.delete(r);
   if(wasDefault)resumes.findAllByUserIdOrderByUploadedAtDesc(uid).stream().findFirst().ifPresent(next->{next.setDefault(true);resumes.save(next);});
 }
 @Transactional public View makeDefault(String uid,String id){
   Resume selected=owned(uid,id);
   for(Resume r:resumes.findAllByUserIdOrderByUploadedAtDesc(uid)){r.setDefault(r.getId().equals(id));resumes.save(r);}
   return view(selected);
 }
 public Path file(String uid,String id){Resume r=owned(uid,id);Path p=directory.resolve(r.getStorageKey()).normalize();if(!p.startsWith(directory))throw new ApiException(HttpStatus.FORBIDDEN,"Invalid file path");return p;}
 @Transactional public void deleteAllForUser(String uid){
   for(Resume r:resumes.findAllByUserIdOrderByUploadedAtDesc(uid)){
     Path file=directory.resolve(r.getStorageKey()).normalize();
     try{Files.deleteIfExists(file);}catch(IOException e){throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,"Unable to delete resume file");}
   }
   resumes.deleteAllByUserId(uid);
 }
 private Resume owned(String uid,String id){Resume r=resumes.findByIdAndUserId(id,uid).orElseThrow(()->ApiException.notFound("Resume"));return r;}
 private static View view(Resume r){return new View(r.getId(),r.getName(),r.getUploadedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString(),r.isDefault(),r.getSize());}
}
