package com.patient.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderLoginResponse {
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
        private ProviderInfo provider;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderInfo {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private String specialization;
        private String verificationStatus;
        private boolean isActive;
    }
} 