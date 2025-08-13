package com.patient.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentListRequest {
    private String start_date; // YYYY-MM-DD
    private String end_date; // YYYY-MM-DD
    private String status; // booked/cancelled/available
    private String appointment_type; // consultation/follow_up/emergency/telemedicine
    private String timezone; // IANA timezone
} 