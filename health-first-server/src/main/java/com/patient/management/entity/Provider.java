package com.patient.management.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "providers")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Provider {
    @Id
    private String id;

    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    @Column(unique = true)
    private String phoneNumber;

    @NotBlank
    private String passwordHash;

    @NotBlank
    @Size(min = 3, max = 100)
    private String specialization;

    @NotBlank
    @Column(unique = true)
    private String licenseNumber;

    @Min(0)
    @Max(50)
    private int yearsOfExperience;

    @Embedded
    private ClinicAddress clinicAddress;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    private String licenseDocumentUrl;

    @Builder.Default
    private boolean isActive = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Column(name = "last_login")
    private Instant lastLogin;

    @Builder.Default
    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Builder.Default
    @Column(name = "login_count")
    private int loginCount = 0;

    public enum VerificationStatus {
        PENDING, VERIFIED, REJECTED
    }

    @Embeddable
    @Data
    public static class ClinicAddress {
        @NotBlank
        @Size(max = 200)
        private String street;
        @NotBlank
        @Size(max = 100)
        private String city;
        @NotBlank
        @Size(max = 50)
        private String state;
        @NotBlank
        private String zip;
    }
} 