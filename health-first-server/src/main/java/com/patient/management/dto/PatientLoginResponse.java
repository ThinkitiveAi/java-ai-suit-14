package com.patient.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientLoginResponse {
    private boolean success;
    private String message;
    private Data data;
    private String errorCode;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private String accessToken;
        private long expiresIn;
        private String tokenType;
        private PatientInfo patient;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientInfo {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String dateOfBirth;
        private boolean emailVerified;
        private boolean phoneVerified;
        private boolean isActive;
    }
} 