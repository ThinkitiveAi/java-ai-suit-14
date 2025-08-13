package com.patient.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilitySummaryDto {
    private int total_slots;
    private int available_slots;
    private int booked_slots;
    private int cancelled_slots;
} 