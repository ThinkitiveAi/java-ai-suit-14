package com.patient.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotDto {
    private String slot_id;
    private String date;
    private String start_time;
    private String end_time;
    private String appointment_type;
    private LocationDto location;
    private PricingDto pricing;
    private List<String> special_requirements;
} 