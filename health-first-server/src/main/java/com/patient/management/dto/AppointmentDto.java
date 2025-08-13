package com.patient.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {
    private String appointment_id;
    private String slot_id;
    private String booking_reference;
    private String provider_id;
    private String provider_name;
    private String provider_specialization;
    private String patient_id;
    private String patient_name;
    private String date;
    private String start_time;
    private String end_time;
    private String status;
    private String appointment_type;
    private LocationDto location;
    private PricingDto pricing;
    private String notes;
    private java.util.List<String> special_requirements;
    private String created_at;
    private String updated_at;
} 