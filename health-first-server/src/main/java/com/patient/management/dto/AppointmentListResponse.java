package com.patient.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentListResponse {
    private boolean success;
    private String message;
    private AppointmentListData data;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppointmentListData {
        private String user_id;
        private String user_type; // provider or patient
        private AppointmentListRequest filters;
        private int total_appointments;
        private List<AppointmentDto> appointments;
    }
} 