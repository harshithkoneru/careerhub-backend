package com.careerhub.service;
import com.careerhub.dto.InterviewDtos.*;
import com.careerhub.entity.*;
import com.careerhub.exception.ApiException;
import com.careerhub.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class InterviewService {
 private final InterviewRepository interviews;private final UserRepository users;private final ApplicationRepository applications;
 public InterviewService(InterviewRepository i,UserRepository u,ApplicationRepository a){interviews=i;users=u;applications=a;}
 public List<View> list(String uid,String mode){
   List<Interview> records;
   if("upcoming".equalsIgnoreCase(mode))records=interviews.findByUserIdAndStatusAndDateGreaterThanEqualOrderByDateAscTimeAsc(uid,InterviewStatus.UPCOMING,LocalDate.now());
   else if("completed".equalsIgnoreCase(mode))records=interviews.findAllByUserIdAndStatusOrderByDateDescTimeDesc(uid,InterviewStatus.COMPLETED);
   else records=interviews.findAllByUserIdOrderByDateAscTimeAsc(uid);
   return records.stream().map(InterviewService::view).toList();
 }
 public View get(String uid,String id){return view(owned(uid,id));}
 @Transactional public View create(String uid,SaveRequest r){Interview i=new Interview();i.setUser(user(uid));apply(i,r,uid);return view(interviews.save(i));}
 @Transactional public View update(String uid,String id,SaveRequest r){Interview i=owned(uid,id);apply(i,r,uid);return view(interviews.save(i));}
 @Transactional public void delete(String uid,String id){interviews.delete(owned(uid,id));}
 private void apply(Interview i,SaveRequest r,String uid){
   if(!List.of("HR","Technical","Coding","Managerial","Final","Other").contains(r.round()))throw ApiException.badRequest("Invalid interview round");
   if(!List.of("Online","Offline","Phone").contains(r.mode()))throw ApiException.badRequest("Invalid interview mode");
   JobApplication linked=null;
   if(r.applicationId()!=null&&!r.applicationId().isBlank()){
     linked=applications.findById(r.applicationId()).orElseThrow(()->ApiException.notFound("Application"));
     if(!linked.getUser().getId().equals(uid))throw new ApiException(HttpStatus.FORBIDDEN,"Linked application is not owned by you");
     if(r.date().isBefore(linked.getApplicationDate()))throw ApiException.badRequest("Interview date cannot be before the application date");
   }
   i.setApplication(linked);i.setCompany(r.company().trim());i.setRole(r.role().trim());i.setDate(r.date());i.setTime(r.time());
   i.setRound(r.round());i.setMode(r.mode());i.setInterviewLink(r.interviewLink());i.setNotes(r.notes());
   try{i.setStatus(r.status()==null||r.status().isBlank()?InterviewStatus.UPCOMING:InterviewStatus.valueOf(r.status().trim().toUpperCase()));}
   catch(IllegalArgumentException e){throw ApiException.badRequest("Invalid interview status");}
 }
 private User user(String id){return users.findById(id).orElseThrow(()->ApiException.notFound("User"));}
 private Interview owned(String uid,String id){Interview i=interviews.findById(id).orElseThrow(()->ApiException.notFound("Interview"));if(!i.getUser().getId().equals(uid))throw new ApiException(HttpStatus.FORBIDDEN,"You do not own this interview");return i;}
 public static View view(Interview i){return new View(i.getId(),i.getApplication()==null?null:i.getApplication().getId(),i.getCompany(),i.getRole(),i.getDate(),i.getTime()==null?null:i.getTime().format(DateTimeFormatter.ofPattern("HH:mm")),i.getRound(),i.getMode(),i.getInterviewLink(),label(i.getStatus()),i.getNotes());}
 private static String label(InterviewStatus s){return s.name().charAt(0)+s.name().substring(1).toLowerCase();}
}
