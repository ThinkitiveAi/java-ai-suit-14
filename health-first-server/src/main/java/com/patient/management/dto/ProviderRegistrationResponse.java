package com.patient.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderRegistrationResponse {
    private boolean success;
    private String message;
    private Data data;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private String providerId;
        private String email;
        private String verificationStatus;
    }
} 