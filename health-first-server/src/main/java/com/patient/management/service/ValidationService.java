package com.patient.management.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class ValidationService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$"); // E.164 format
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    private static final Pattern LICENSE_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");
    private static final Set<String> SPECIALIZATIONS = new HashSet<>(Arrays.asList(
            "Cardiology", "Dermatology", "Neurology", "Pediatrics", "Oncology", "Orthopedics", "Psychiatry", "Radiology", "General Medicine"
    ));

    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public boolean isValidLicense(String license) {
        return license != null && LICENSE_PATTERN.matcher(license).matches();
    }

    public boolean isValidSpecialization(String specialization) {
        return specialization != null && SPECIALIZATIONS.contains(specialization.trim());
    }
} 