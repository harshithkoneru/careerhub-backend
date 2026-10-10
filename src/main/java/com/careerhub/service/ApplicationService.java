package com.careerhub.service;
import com.careerhub.dto.ApplicationDtos.*;
import com.careerhub.entity.*;
import com.careerhub.exception.ApiException;
import com.careerhub.repository.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplicationService {
 private final ApplicationRepository apps; private final UserRepository users; private final InterviewRepository interviews;
 private static final Map<ApplicationStatus,Set<ApplicationStatus>> NEXT=Map.of(
   ApplicationStatus.APPLIED,Set.of(ApplicationStatus.SHORTLISTED,ApplicationStatus.REJECTED),
   ApplicationStatus.SHORTLISTED,Set.of(ApplicationStatus.INTERVIEW,ApplicationStatus.REJECTED),
   ApplicationStatus.INTERVIEW,Set.of(ApplicationStatus.SELECTED,ApplicationStatus.REJECTED),
   ApplicationStatus.SELECTED,Set.of(),ApplicationStatus.REJECTED,Set.of());
 public ApplicationService(ApplicationRepository apps,UserRepository users,InterviewRepository interviews){this.apps=apps;this.users=users;this.interviews=interviews;}
 public Page<View> list(String uid,String status,String q,String jobRole,int page,int size,String sort,String direction) {
   if(page<0 || size<1 || size>100) throw ApiException.badRequest("page must be >= 0 and size must be between 1 and 100");
   Set<String> sortable=Set.of("applicationDate","company","role","status","createdAt");
   if(!sortable.contains(sort)) throw ApiException.badRequest("Unsupported sort field");
   if(!"asc".equalsIgnoreCase(direction)&&!"desc".equalsIgnoreCase(direction)) throw ApiException.badRequest("direction must be asc or desc");
   Sort order=Sort.by("desc".equalsIgnoreCase(direction)?Sort.Direction.DESC:Sort.Direction.ASC,sort);
   ApplicationStatus wanted=status==null||status.isBlank()?null:parseStatus(status);
   Specification<JobApplication> spec=(root,query,cb)->{
     List<Predicate> predicates=new ArrayList<>(); predicates.add(cb.equal(root.get("user").get("id"),uid));
     if(wanted!=null) predicates.add(cb.equal(root.get("status"),wanted));
     if(q!=null&&!q.isBlank()){String pattern="%"+q.trim().toLowerCase()+"%";predicates.add(cb.or(cb.like(cb.lower(root.get("company")),pattern),cb.like(cb.lower(root.get("role")),pattern)));}
     if(jobRole!=null&&!jobRole.isBlank())predicates.add(cb.like(cb.lower(root.get("role")),"%"+jobRole.trim().toLowerCase()+"%"));
     return cb.and(predicates.toArray(Predicate[]::new));
   };
   return apps.findAll(spec,PageRequest.of(page,size,order)).map(ApplicationService::view);
 }
 public View get(String uid,String id){return view(owned(uid,id));}
 @Transactional public View create(String uid,SaveRequest r) {
   if(apps.existsByUserIdAndCompanyIgnoreCaseAndRoleIgnoreCase(uid,r.company().trim(),r.role().trim())) throw ApiException.conflict("An application for this company and role already exists");
   JobApplication a=new JobApplication(); a.setUser(user(uid)); apply(a,r,true); return view(apps.save(a));
 }
 @Transactional public View update(String uid,String id,SaveRequest r) {
   JobApplication a=owned(uid,id);
   if(apps.existsByUserIdAndCompanyIgnoreCaseAndRoleIgnoreCaseAndIdNot(uid,r.company().trim(),r.role().trim(),id)) throw ApiException.conflict("An application for this company and role already exists");
   apply(a,r,false); return view(apps.save(a));
 }
 @Transactional public void delete(String uid,String id){
   JobApplication application=owned(uid,id);
   interviews.findAll().stream().filter(i->i.getApplication()!=null&&i.getApplication().getId().equals(id)).forEach(i->{i.setApplication(null);interviews.save(i);});
   apps.delete(application);
 }
 private void apply(JobApplication a,SaveRequest r,boolean fresh) {
   ApplicationStatus requested=parseStatus(r.status());
   if(!fresh && requested!=a.getStatus()&&!NEXT.get(a.getStatus()).contains(requested))
      throw ApiException.badRequest("Invalid application status transition from "+a.getStatus()+" to "+requested);
   a.setCompany(r.company().trim()); a.setRole(r.role().trim()); a.setJobType(r.jobType()); a.setLocation(r.location());
   a.setApplicationDate(r.applicationDate()); a.setJobUrl(r.jobUrl()); a.setSalary(r.salary()); a.setRequiredSkills(join(r.requiredSkills()));
   a.setRelevantSkills(join(r.relevantSkills())); a.setResumeVersion(r.resumeVersion()); a.setStatus(requested);
   a.setRejectionReason(parseRejection(r.rejectionReason())); a.setNotes(r.notes());
 }
 private User user(String id){return users.findById(id).orElseThrow(()->ApiException.notFound("User"));}
 private JobApplication owned(String uid,String id){JobApplication a=apps.findById(id).orElseThrow(()->ApiException.notFound("Application"));if(!a.getUser().getId().equals(uid))throw new ApiException(HttpStatus.FORBIDDEN,"You do not own this application");return a;}
 public List<JobApplication> entities(String uid){return apps.findAllByUserId(uid);}
 public static ApplicationStatus parseStatus(String s){if(s==null||s.isBlank())return ApplicationStatus.APPLIED; try{return ApplicationStatus.valueOf(s.trim().toUpperCase().replace(' ','_'));}catch(Exception e){throw ApiException.badRequest("Invalid application status");}}
 public static RejectionReason parseRejection(String s){if(s==null||s.isBlank())return RejectionReason.UNKNOWN;try{return RejectionReason.valueOf(s.trim().toUpperCase().replace(' ','_'));}catch(Exception e){return RejectionReason.OTHER;}}
 public static View view(JobApplication a) {
   return new View(a.getId(),a.getCompany(),a.getRole(),a.getJobType(),a.getLocation(),a.getApplicationDate(),a.getJobUrl(),a.getSalary(),
     split(a.getRequiredSkills()),split(a.getRelevantSkills()),a.getResumeVersion(),label(a.getStatus()),label(a.getRejectionReason()),a.getNotes(),timeline(a.getStatus()));
 }
 private static List<String> split(String s){return s==null||s.isBlank()?List.of():Arrays.stream(s.split(",")).map(String::trim).filter(x->!x.isEmpty()).toList();}
 private static String join(List<String> values){return values==null?null:values.stream().map(String::trim).filter(x->!x.isEmpty()).collect(Collectors.joining(", "));}
 private static String label(Enum<?> e){if(e==null)return "Unknown";String[] w=e.name().toLowerCase().split("_");return Arrays.stream(w).map(x->Character.toUpperCase(x.charAt(0))+x.substring(1)).collect(Collectors.joining(" "));}
 private static List<String> timeline(ApplicationStatus status){List<ApplicationStatus> steps=List.of(ApplicationStatus.APPLIED,ApplicationStatus.SHORTLISTED,ApplicationStatus.INTERVIEW,ApplicationStatus.SELECTED);List<String> out=new ArrayList<>();for(ApplicationStatus s:steps){if(s==ApplicationStatus.SHORTLISTED&&status==ApplicationStatus.REJECTED)break;out.add(label(s));if(s==status)break;}if(status==ApplicationStatus.REJECTED)out.add("Rejected");return out;}
}
