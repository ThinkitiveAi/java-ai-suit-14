package com.patient.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "provider_availability")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderAvailability {
    @Id
    private String id;

    @NotBlank
    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotBlank
    @Column(name = "timezone", nullable = false)
    private String timezone; // IANA timezone ID

    @NotBlank
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$")
    @Column(name = "start_time", nullable = false)
    private String startTime; // HH:mm, provider local time

    @NotBlank
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$")
    @Column(name = "end_time", nullable = false)
    private String endTime; // HH:mm, provider local time

    @Builder.Default
    @Column(name = "is_recurring", nullable = false)
    private boolean isRecurring = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_pattern")
    private RecurrencePattern recurrencePattern; // daily/weekly/monthly

    @Column(name = "recurrence_end_date")
    private LocalDate recurrenceEndDate;

    @Builder.Default
    @Min(5)
    @Max(480)
    @Column(name = "slot_duration", nullable = false)
    private int slotDuration = 30; // minutes

    @Builder.Default
    @Min(0)
    @Max(120)
    @Column(name = "break_duration", nullable = false)
    private int breakDuration = 0; // minutes between slots

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", nullable = false)
    private AvailabilityStatus status = AvailabilityStatus.AVAILABLE;

    @Builder.Default
    @Min(1)
    @Max(10)
    @Column(name = "max_appointments_per_slot", nullable = false)
    private int maxAppointmentsPerSlot = 1;

    @Builder.Default
    @Min(0)
    @Column(name = "current_appointments", nullable = false)
    private int currentAppointments = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "appointment_type", nullable = false)
    private AppointmentType appointmentType = AppointmentType.CONSULTATION;

    @Embedded
    private Location location;

    @Embedded
    private Pricing pricing;

    @Size(max = 500)
    @Column(name = "notes", length = 500)
    private String notes;

    @ElementCollection
    @CollectionTable(name = "availability_special_requirements", joinColumns = @JoinColumn(name = "availability_id"))
    @Column(name = "requirement")
    private List<String> specialRequirements;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum RecurrencePattern { DAILY, WEEKLY, MONTHLY }

    public enum AvailabilityStatus { AVAILABLE, BOOKED, CANCELLED, BLOCKED, MAINTENANCE }

    public enum AppointmentType { CONSULTATION, FOLLOW_UP, EMERGENCY, TELEMEDICINE }

    @Embeddable
    @Data
    public static class Location {
        @Enumerated(EnumType.STRING)
        @Column(name = "location_type")
        private LocationType type; // clinic/hospital/telemedicine/home_visit
        @Column(name = "address")
        private String address;
        @Column(name = "room_number")
        private String roomNumber;
    }

    public enum LocationType { CLINIC, HOSPITAL, TELEMEDICINE, HOME_VISIT }

    @Embeddable
    @Data
    public static class Pricing {
        @Column(name = "base_fee")
        private BigDecimal baseFee;
        @Column(name = "insurance_accepted")
        private Boolean insuranceAccepted;
        @Column(name = "currency")
        private String currency = "USD";
    }
} 