package com.careerhub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "job_applications", indexes = {@Index(name="idx_app_user_date", columnList="user_id,application_date")})
@Getter @Setter @NoArgsConstructor
public class JobApplication {
    @Id @Column(length = 36) private String id = UUID.randomUUID().toString();
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(nullable=false, length=160) private String company;
    @Column(nullable=false, length=160) private String role;
    @Column(length=50) private String jobType;
    @Column(length=120) private String location;
    @Column(name="application_date", nullable=false) private LocalDate applicationDate;
    @Column(length=1000) private String jobUrl;
    @Column(length=80) private String salary;
    @Column(length=2000) private String requiredSkills;
    @Column(length=2000) private String relevantSkills;
    @Column(length=180) private String resumeVersion;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private ApplicationStatus status = ApplicationStatus.APPLIED;
    @Enumerated(EnumType.STRING) @Column(length=32) private RejectionReason rejectionReason = RejectionReason.UNKNOWN;
    @Column(length=3000) private String notes;
    @Column(nullable=false, updatable=false) private Instant createdAt = Instant.now();
    @Column(nullable=false) private Instant updatedAt = Instant.now();

    @PreUpdate
    void updateTimestamp() {
        updatedAt = Instant.now();
    }
}
