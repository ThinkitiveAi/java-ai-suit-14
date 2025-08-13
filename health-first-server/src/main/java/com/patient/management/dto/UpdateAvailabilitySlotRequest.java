package com.patient.management.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateAvailabilitySlotRequest {
    private String start_time; // HH:mm (in provider's TZ for that day)
    private String end_time; // HH:mm
    private String status; // available/booked/cancelled/blocked
    private String notes;

    private UpdatePricingDto pricing;
} 