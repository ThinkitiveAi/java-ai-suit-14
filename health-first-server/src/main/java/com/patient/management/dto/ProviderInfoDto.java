package com.patient.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderInfoDto {
    private String id;
    private String name;
    private String specialization;
    private int years_of_experience;
    private double rating;
    private String clinic_address;
} 