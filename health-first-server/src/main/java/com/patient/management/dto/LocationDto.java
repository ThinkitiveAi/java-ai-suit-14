package com.patient.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private String type; // clinic/hospital/telemedicine/home_visit
    private String address;
    private String room_number;
} 