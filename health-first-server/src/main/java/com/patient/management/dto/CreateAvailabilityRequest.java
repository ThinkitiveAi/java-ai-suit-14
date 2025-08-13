package com.patient.management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateAvailabilityRequest {
    @NotBlank
    private String date; // YYYY-MM-DD
    @NotBlank
    private String start_time; // HH:mm
    @NotBlank
    private String end_time; // HH:mm
    @NotBlank
    private String timezone; // IANA TZ

    private Integer slot_duration; // minutes
    private Integer break_duration; // minutes
    private Boolean is_recurring;
    private String recurrence_pattern; // daily/weekly/monthly
    private String recurrence_end_date; // YYYY-MM-DD
    private String appointment_type; // consultation/follow_up/emergency/telemedicine

    private LocationDto location;
    private PricingDto pricing;

    private List<String> special_requirements;

    @Size(max = 500)
    private String notes;
} 