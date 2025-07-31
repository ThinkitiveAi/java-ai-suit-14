package com.patient.management.dto;

import lombok.Data;

@Data
public class ProviderLoginRequest {
    private String identifier; // email or phone
    private String password;
    private Boolean rememberMe;
} 