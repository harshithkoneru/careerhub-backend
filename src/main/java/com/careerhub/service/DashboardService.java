package com.careerhub.service;
import com.careerhub.entity.*;
import com.careerhub.dto.InterviewDtos;
import com.careerhub.repository.*;
import org.springframework.stereotype.Service;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class DashboardService {
 private final ApplicationRepository apps;private final InterviewRepository interviews;private final CareerAnalysisService analysis;
 public DashboardService(ApplicationRepository a,InterviewRepository i,CareerAnalysisService c){apps=a;interviews=i;analysis=c;}
 public Map<String,Object> data(String uid){
  List<JobApplication> all=apps.findAllByUserId(uid);
  long total=all.size(),shortlisted=count(all,ApplicationStatus.SHORTLISTED),inInterview=count(all,ApplicationStatus.INTERVIEW),selected=count(all,ApplicationStatus.SELECTED),rejected=count(all,ApplicationStatus.REJECTED);
  double conversionRate=pct(inInterview+selected,total);
  Map<String,Object> stats=Map.of("totalApplications",total,"shortlisted",shortlisted,"interviews",inInterview,"selected",selected,"rejected",rejected,
    "interviewConversion",conversionRate,"interviewConversionRate",conversionRate,"selectionRate",pct(selected,total));
  List<Map<String,Object>> statuses=Arrays.stream(ApplicationStatus.values()).map(s->Map.<String,Object>of("name",label(s),"value",count(all,s))).toList();
  Map<YearMonth,Long> months=all.stream().collect(Collectors.groupingBy(a->YearMonth.from(a.getApplicationDate()),TreeMap::new,Collectors.counting()));
  List<Map<String,Object>> monthly=months.entrySet().stream().map(e->Map.<String,Object>of("month",e.getKey().getMonth().getDisplayName(TextStyle.SHORT,Locale.ENGLISH),"applications",e.getValue())).toList();
  List<Map<String,Object>> recent=all.stream().sorted(Comparator.comparing(JobApplication::getApplicationDate).reversed()).limit(5).map(a->Map.<String,Object>of("id",a.getId(),"company",a.getCompany(),"role",a.getRole(),"applicationDate",a.getApplicationDate(),"status",label(a.getStatus()),"resumeVersion",Objects.toString(a.getResumeVersion(),""))).toList();
  List<Interview> next=interviews.findByUserIdAndStatusAndDateGreaterThanEqualOrderByDateAscTimeAsc(uid,InterviewStatus.UPCOMING,java.time.LocalDate.now());
  List<InterviewDtos.View> upcoming=next.stream().limit(5).map(InterviewService::view).toList();
  return Map.of("stats",stats,"statusChart",statuses,"monthlyApplications",monthly,"rolePerformance",analysis.rolePerformance(uid),"rejectionReasons",analysis.rejectionAnalysis(uid),"recentApplications",recent,"upcomingInterviews",upcoming);
 }
 private static long count(List<JobApplication>a,ApplicationStatus s){return a.stream().filter(x->x.getStatus()==s).count();}
 private static double pct(long n,long d){return d==0?0:Math.round(n*1000.0/d)/10.0;}
 private static String label(ApplicationStatus s){return Arrays.stream(s.name().toLowerCase().split("_")).map(x->Character.toUpperCase(x.charAt(0))+x.substring(1)).collect(Collectors.joining(" "));}
}
