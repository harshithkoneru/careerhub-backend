package com.careerhub.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;
public final class InterviewDtos {
 private InterviewDtos() {}
 public record SaveRequest(String applicationId,@NotBlank @Size(max=160) String company,@NotBlank @Size(max=160) String role,
   @NotNull LocalDate date,LocalTime time,@NotBlank @Size(max=40) String round,@NotBlank @Size(max=20) String mode,
   @Size(max=1000) String interviewLink,@Size(max=20) String status,@Size(max=3000) String notes) {}
 public record View(String id,String applicationId,String company,String role,LocalDate date,String time,
   String round,String mode,String interviewLink,String status,String notes) {}
}
