package com.careerhub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="resumes")
@Getter @Setter @NoArgsConstructor
public class Resume {
    @Id @Column(length=36) private String id = UUID.randomUUID().toString();
    @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="user_id", nullable=false) private User user;
    @Column(nullable=false, length=255) private String name;
    @Column(nullable=false, length=500) private String storageKey;
    @Column(nullable=false) private long size;
    @Column(nullable=false) private boolean isDefault;
    @Column(nullable=false, updatable=false) private Instant uploadedAt = Instant.now();
}
