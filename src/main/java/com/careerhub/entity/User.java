package com.careerhub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email"))
@Getter @Setter @NoArgsConstructor
public class User {
    @Id @Column(length = 36) private String id = UUID.randomUUID().toString();
    @Column(nullable = false, length = 120) private String fullName;
    @Column(nullable = false, length = 190) private String email;
    @Column(nullable = false) private String password;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private Role role = Role.USER;
    @Column(length = 40) private String phone;
    @Column(length = 160) private String college;
    @Column(length = 160) private String degree;
    private Integer graduationYear;
    @Column(length = 1500) private String skills;
    @Column(length = 500) private String linkedIn;
    @Column(length = 500) private String github;
    @Column(length = 500) private String portfolio;
    @Column(nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(nullable = false) private Instant updatedAt = Instant.now();

    @PreUpdate
    void updateTimestamp() {
        updatedAt = Instant.now();
    }

    public User(String fullName, String email, String password, Role role) {
        this.fullName = fullName; this.email = email; this.password = password; this.role = role;
    }
}
