package com.careerhub.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
public final class ApplicationDtos {
 private ApplicationDtos() {}
 public record SaveRequest(@NotBlank @Size(max=160) String company,@NotBlank @Size(max=160) String role,
   @Size(max=50) String jobType,@Size(max=120) String location,@NotNull LocalDate applicationDate,
   @Size(max=1000) String jobUrl,@Size(max=80) String salary,@Size(max=30) List<@Size(max=100) String> requiredSkills,
   @Size(max=30) List<@Size(max=100) String> relevantSkills,@Size(max=180) String resumeVersion,@Size(max=24) String status,
   @Size(max=40) String rejectionReason,@Size(max=3000) String notes) {}
 public record View(String id,String company,String role,String jobType,String location,LocalDate applicationDate,
   String jobUrl,String salary,List<String> requiredSkills,List<String> relevantSkills,String resumeVersion,
   String status,String rejectionReason,String notes,List<String> timeline) {}
}
