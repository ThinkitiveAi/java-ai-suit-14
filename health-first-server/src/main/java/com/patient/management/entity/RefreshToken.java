package com.patient.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    private String id;

    @Column(nullable = false)
    private String providerId;

    @Column(nullable = false)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Builder.Default
    private boolean isRevoked = false;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant lastUsedAt;
} 