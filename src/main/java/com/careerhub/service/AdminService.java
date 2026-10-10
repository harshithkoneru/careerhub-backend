package com.careerhub.service;
import com.careerhub.entity.*;
import com.careerhub.exception.ApiException;
import com.careerhub.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class AdminService {
 private final UserRepository users;private final ApplicationRepository apps;private final InterviewRepository interviews;private final ResumeService resumeFiles;
 public AdminService(UserRepository u,ApplicationRepository a,InterviewRepository i,ResumeService rf){users=u;apps=a;interviews=i;resumeFiles=rf;}
 public Map<String,Object> stats(){
  List<JobApplication> all=apps.findAll();
  return Map.of("totalUsers",users.count(),"totalApplications",apps.count(),"totalInterviews",interviews.count(),"totalSelected",all.stream().filter(a->a.getStatus()==ApplicationStatus.SELECTED).count());
 }
 public List<Map<String,Object>> popularRoles(){Map<String,Long> roles=apps.findAll().stream().collect(Collectors.groupingBy(JobApplication::getRole,Collectors.counting()));return roles.entrySet().stream().sorted(Map.Entry.<String,Long>comparingByValue().reversed()).limit(8).map(e->Map.<String,Object>of("name",e.getKey(),"value",e.getValue())).toList();}
 public List<Map<String,Object>> userTrends(){return users.findAll().stream().collect(Collectors.groupingBy(u->u.getCreatedAt().toString().substring(0,7),TreeMap::new,Collectors.counting())).entrySet().stream().map(e->Map.<String,Object>of("month",e.getKey(),"users",e.getValue())).toList();}
 public List<Map<String,Object>> applicationStatuses(){List<JobApplication> all=apps.findAll();return Arrays.stream(ApplicationStatus.values()).map(s->Map.<String,Object>of("name",s.name(),"value",all.stream().filter(a->a.getStatus()==s).count())).toList();}
 public List<Map<String,Object>> userList(){
   return users.findAll().stream().map(u->Map.<String,Object>of("id",u.getId(),"name",u.getFullName(),"email",u.getEmail(),"college",Objects.toString(u.getCollege(),""),"applications",apps.findAllByUserId(u.getId()).size(),"status","Active","role",u.getRole().name())).toList();
 }
 @Transactional public void deleteUser(String id){
   User user=users.findById(id).orElseThrow(()->ApiException.notFound("User"));
   if(user.getRole()==Role.ADMIN)throw ApiException.badRequest("Administrator accounts cannot be removed through this endpoint");
   interviews.deleteAllByUserId(id);apps.deleteAllByUserId(id);resumeFiles.deleteAllForUser(id);users.delete(user);
 }
}
