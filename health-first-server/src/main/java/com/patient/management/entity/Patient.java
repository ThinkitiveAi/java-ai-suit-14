package com.patient.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patients", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email"),
    @UniqueConstraint(columnNames = "phoneNumber")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    private String id;

    @NotBlank @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank @Email
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String passwordHash;

    @NotNull @Past
    private LocalDate dateOfBirth;

    @NotNull @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    private Address address;

    @Embedded
    private EmergencyContact emergencyContact;

    @ElementCollection
    private List<String> medicalHistory;

    @Embedded
    private InsuranceInfo insuranceInfo;

    @Builder.Default
    private boolean emailVerified = false;
    @Builder.Default
    private boolean phoneVerified = false;
    @Builder.Default
    private boolean isActive = true;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    public enum Gender { MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY }

    @Embeddable @Data
    public static class Address {
        @NotBlank @Size(max = 200) private String street;
        @NotBlank @Size(max = 100) private String city;
        @NotBlank @Size(max = 50) private String state;
        @NotBlank private String zip;
    }

    @Embeddable @Data
    public static class EmergencyContact {
        @Size(max = 100) private String name;
        private String phone;
        @Size(max = 50) private String relationship;
    }

    @Embeddable @Data
    public static class InsuranceInfo {
        private String provider;
        private String policyNumber;
    }
} 