package com.patient.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotDto {
    private String slot_id;
    private String start_time;
    private String end_time;
    private String status;
    private String appointment_type;
    private LocationDto location;
    private PricingDto pricing;
} 