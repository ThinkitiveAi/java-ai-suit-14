package com.patient.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "appointment_slots", indexes = {
        @Index(name = "idx_provider_time", columnList = "provider_id, slot_start_time, slot_end_time"),
        @Index(name = "idx_availability_id", columnList = "availability_id")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSlot {
    @Id
    private String id;

    @NotBlank
    @Column(name = "availability_id", nullable = false)
    private String availabilityId;

    @NotBlank
    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @NotNull
    @Column(name = "slot_start_time", nullable = false)
    private Instant slotStartTime; // UTC

    @NotNull
    @Column(name = "slot_end_time", nullable = false)
    private Instant slotEndTime; // UTC

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", nullable = false)
    private SlotStatus status = SlotStatus.AVAILABLE;

    @Column(name = "patient_id")
    private String patientId; // nullable until booked

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "appointment_type", nullable = false)
    private ProviderAvailability.AppointmentType appointmentType = ProviderAvailability.AppointmentType.CONSULTATION;

    @Column(name = "booking_reference", unique = true)
    private String bookingReference;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum SlotStatus { AVAILABLE, BOOKED, CANCELLED, BLOCKED }
} 