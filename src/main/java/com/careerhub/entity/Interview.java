package com.careerhub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="interviews", indexes=@Index(name="idx_interview_user_date", columnList="user_id,interview_date"))
@Getter @Setter @NoArgsConstructor
public class Interview {
    @Id @Column(length=36) private String id = UUID.randomUUID().toString();
    @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="user_id", nullable=false) private User user;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="application_id") private JobApplication application;
    @Column(nullable=false, length=160) private String company;
    @Column(nullable=false, length=160) private String role;
    @Column(name="interview_date", nullable=false) private LocalDate date;
    private LocalTime time;
    @Column(length=40) private String round;
    @Column(length=20) private String mode;
    @Column(length=1000) private String interviewLink;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=16) private InterviewStatus status = InterviewStatus.UPCOMING;
    @Column(length=3000) private String notes;
    @Column(nullable=false, updatable=false) private Instant createdAt = Instant.now();
    @Column(nullable=false) private Instant updatedAt = Instant.now();

    @PreUpdate
    void updateTimestamp() {
        updatedAt = Instant.now();
    }
}
