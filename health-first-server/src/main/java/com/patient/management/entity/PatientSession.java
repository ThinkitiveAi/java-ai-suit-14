package com.patient.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "patient_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientSession {
    @Id
    private String id;

    @Column(nullable = false)
    private String patientId;

    @Column(nullable = false)
    private String refreshTokenHash;

    @Lob
    private String deviceInfo; // JSON string

    private String ipAddress;
    private String userAgent;
    private Instant expiresAt;
    @Builder.Default
    private boolean isRevoked = false;
    @Column(nullable = false)
    private Instant createdAt;
    private Instant lastUsedAt;
    @Lob
    private String locationInfo; // JSON string, optional
} 