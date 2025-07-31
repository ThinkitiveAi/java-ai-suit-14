package com.patient.management.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class ProviderRegistrationRequest {
    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;
    @NotBlank
    @Size(min = 2, max = 50)
    private String lastName;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
    @NotBlank
    private String confirmPassword;
    @NotBlank
    @Size(min = 3, max = 100)
    private String specialization;
    @NotBlank
    private String licenseNumber;
    @Min(0)
    @Max(50)
    private int yearsOfExperience;
    @NotNull
    private ClinicAddress clinicAddress;

    @Data
    public static class ClinicAddress {
        @NotBlank
        @Size(max = 200)
        private String street;
        @NotBlank
        @Size(max = 100)
        private String city;
        @NotBlank
        @Size(max = 50)
        private String state;
        @NotBlank
        private String zip;
    }
} 