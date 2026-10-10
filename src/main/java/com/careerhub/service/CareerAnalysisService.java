package com.careerhub.service;
import com.careerhub.entity.*;
import com.careerhub.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CareerAnalysisService {
 private final ApplicationRepository applications;
 public CareerAnalysisService(ApplicationRepository a){applications=a;}
 public Map<String,Object> analysis(String uid){
   List<JobApplication> all=applications.findAllByUserId(uid);int total=all.size();
   long rejected=count(all,ApplicationStatus.REJECTED),shortlisted=count(all,ApplicationStatus.SHORTLISTED),interview=count(all,ApplicationStatus.INTERVIEW),selected=count(all,ApplicationStatus.SELECTED);
   List<Map<String,Object>> byRole=rolePerformance(uid);
   Map<String,Object> best=byRole.stream().max(Comparator.comparingDouble(x->((Number)x.get("rate")).doubleValue())).orElse(Map.of("role","N/A","rate",0));
   Map<String,Long> reasons=all.stream().filter(a->a.getStatus()==ApplicationStatus.REJECTED).collect(Collectors.groupingBy(a->label(a.getRejectionReason()),Collectors.counting()));
   String mainReason=reasons.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("No rejections recorded");
   double conversion=percent(interview+selected+shortlisted,total);
   String issue=conversion<20?"SKILL / JOB MATCH":"INTERVIEW PREPARATION";
   int score=(int)Math.round(Math.min(100,Math.max(0,percent(selected*3+interview*2+shortlisted,total)*0.55+Math.min(total,20)*2)));
   List<String> improvements=List.of("Tailor your resume to each role","Strengthen skills mentioned in job descriptions","Practice technical and behavioral interviews");
   List<Map<String,Object>> metrics=List.of(metric("Application Activity",Math.min(100,total*5)),metric("Interview Conversion",Math.min(100,(int)conversion*2)),metric("Shortlist Rate",Math.min(100,(int)percent(shortlisted,total)*2)),metric("Selection Rate",Math.min(100,(int)percent(selected,total)*4)),metric("Follow-up Activity",Math.min(100,total==0?0:65)));
   Map<String,Object> careerHealth=Map.of("score",score,"metrics",metrics);
   return Map.ofEntries(Map.entry("totalApplications",total),Map.entry("rejected",rejected),Map.entry("shortlisted",shortlisted),Map.entry("interviews",interview),Map.entry("selected",selected),Map.entry("interviewConversion",round(conversion)),Map.entry("mainIssue",issue),Map.entry("mainIssueDescription",total==0?"Add your first application to receive personalized insights.":"Review role fit, resume alignment, and interview conversion to identify your next improvement."),Map.entry("bestRole",best.get("role")),Map.entry("bestRoleRate",best.get("rate")),Map.entry("mainImprovementArea",mainReason),Map.entry("improvementList",improvements),Map.entry("recommendations",recommendations(mainReason,conversion)),Map.entry("careerHealth",careerHealth),Map.entry("rolePerformance",byRole),Map.entry("rejectionAnalysis",reasonDataset(reasons)));
 }
 public List<Map<String,Object>> rolePerformance(String uid){
   Map<String,List<JobApplication>> groups=applications.findAllByUserId(uid).stream().collect(Collectors.groupingBy(JobApplication::getRole,TreeMap::new,Collectors.toList()));
   return groups.entrySet().stream().map(e->{long n=e.getValue().size();long wins=e.getValue().stream().filter(a->a.getStatus()==ApplicationStatus.INTERVIEW||a.getStatus()==ApplicationStatus.SELECTED).count();return Map.<String,Object>of("role",e.getKey(),"applications",n,"interviews",wins,"rate",round(percent(wins,n)));}).toList();
 }
 public List<Map<String,Object>> rejectionAnalysis(String uid){List<JobApplication> all=applications.findAllByUserId(uid);Map<String,Long> reasons=all.stream().filter(a->a.getStatus()==ApplicationStatus.REJECTED).collect(Collectors.groupingBy(a->label(a.getRejectionReason()),Collectors.counting()));return reasonDataset(reasons);}
 private List<Map<String,Object>> reasonDataset(Map<String,Long> data){long total=data.values().stream().mapToLong(Long::longValue).sum();return data.entrySet().stream().map(e->Map.<String,Object>of("name",e.getKey(),"value",round(percent(e.getValue(),total)),"count",e.getValue())).toList();}
 private List<Map<String,Object>> recommendations(String reason,double conversion){List<Map<String,Object>> out=new ArrayList<>();if(conversion<20)out.add(Map.of("title","Focus on role alignment","description","Prioritize roles that match your strongest skills and tailor each resume to the job description."));if(reason.contains("Skills"))out.add(Map.of("title","Address recurring skill gaps","description","Build demonstrable projects and add evidence for the skills appearing in target job descriptions."));out.add(Map.of("title","Prepare structured interview stories","description","Practice concise examples covering impact, collaboration, and problem solving."));return out;}
 private static Map<String,Object> metric(String label,int value){return Map.of("label",label,"value",value);}
 private static long count(List<JobApplication> a,ApplicationStatus s){return a.stream().filter(x->x.getStatus()==s).count();}
 private static double percent(double n,double d){return d==0?0:n*100.0/d;}
 private static double round(double n){return Math.round(n*10.0)/10.0;}
 private static String label(RejectionReason r){if(r==null)return "Unknown";return Arrays.stream(r.name().toLowerCase().split("_")).map(x->Character.toUpperCase(x.charAt(0))+x.substring(1)).collect(Collectors.joining(" "));}
}
