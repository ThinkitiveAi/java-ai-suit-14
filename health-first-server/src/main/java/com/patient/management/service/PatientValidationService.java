package com.patient.management.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class PatientValidationService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$"); // E.164 format
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    private static final Pattern ZIP_PATTERN = Pattern.compile("^[0-9]{5}(?:-[0-9]{4})?$", Pattern.CASE_INSENSITIVE);
    private static final Set<String> GENDERS = new HashSet<>(Arrays.asList(
            "male", "female", "other", "prefer_not_to_say"
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
    public boolean isValidZip(String zip) {
        return zip != null && ZIP_PATTERN.matcher(zip).matches();
    }
    public boolean isValidGender(String gender) {
        return gender != null && GENDERS.contains(gender.trim().toLowerCase());
    }
    public boolean isValidAge(LocalDate dob) {
        if (dob == null) return false;
        int age = Period.between(dob, LocalDate.now()).getYears();
        return age >= 13;
    }
} 