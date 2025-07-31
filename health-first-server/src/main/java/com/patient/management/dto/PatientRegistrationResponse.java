package com.patient.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRegistrationResponse {
    private boolean success;
    private String message;
    private Data data;
    private String errorCode;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private String patientId;
        private String email;
        private String phoneNumber;
        private boolean emailVerified;
        private boolean phoneVerified;
    }
} 