package com.patient.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookAppointmentRequest {
    @NotBlank
    private String slot_id;
    @NotBlank
    private String patient_id;
} 