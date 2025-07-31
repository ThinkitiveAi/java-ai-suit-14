package com.patient.management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PatientRegistrationRequest {
    @NotBlank @Size(min = 2, max = 50) private String firstName;
    @NotBlank @Size(min = 2, max = 50) private String lastName;
    @NotBlank @Email private String email;
    @NotBlank private String phoneNumber;
    @NotBlank @Size(min = 8, max = 100) private String password;
    @NotBlank private String confirmPassword;
    @NotNull @Past private LocalDate dateOfBirth;
    @NotBlank private String gender;
    @NotNull private Address address;
    private EmergencyContact emergencyContact;
    private List<String> medicalHistory;
    private InsuranceInfo insuranceInfo;

    @Data
    public static class Address {
        @NotBlank @Size(max = 200) private String street;
        @NotBlank @Size(max = 100) private String city;
        @NotBlank @Size(max = 50) private String state;
        @NotBlank private String zip;
    }
    @Data
    public static class EmergencyContact {
        @Size(max = 100) private String name;
        private String phone;
        @Size(max = 50) private String relationship;
    }
    @Data
    public static class InsuranceInfo {
        private String provider;
        private String policyNumber;
    }
} 