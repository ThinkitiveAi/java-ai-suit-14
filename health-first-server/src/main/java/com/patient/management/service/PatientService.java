package com.patient.management.service;

import com.patient.management.dto.PatientRegistrationRequest;
import com.patient.management.dto.PatientRegistrationResponse;
import com.patient.management.entity.Patient;
import com.patient.management.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientValidationService validationService;

    @Transactional
    public PatientRegistrationResponse register(PatientRegistrationRequest req) {
        Map<String, String> errors = new HashMap<>();
        // Normalize input
        String email = req.getEmail() != null ? req.getEmail().trim().toLowerCase() : null;
        String phone = req.getPhoneNumber() != null ? req.getPhoneNumber().trim() : null;
        String gender = req.getGender() != null ? req.getGender().trim().toLowerCase() : null;

        // Validation
        if (!validationService.isValidEmail(email)) errors.put("email", "Invalid email format");
        if (!validationService.isValidPhone(phone)) errors.put("phone_number", "Invalid phone number format");
        if (!validationService.isValidPassword(req.getPassword())) errors.put("password", "Password must be 8+ chars, upper, lower, number, special char");
        if (!req.getPassword().equals(req.getConfirmPassword())) errors.put("confirm_password", "Passwords do not match");
        if (!validationService.isValidGender(gender)) errors.put("gender", "Invalid gender");
        if (!validationService.isValidAge(req.getDateOfBirth())) errors.put("date_of_birth", "Must be at least 13 years old");
        if (req.getAddress() == null) errors.put("address", "Address required");
        else {
            if (!validationService.isValidZip(req.getAddress().getZip())) errors.put("address.zip", "Invalid zip code");
        }
        // Required fields
        if (req.getFirstName() == null || req.getFirstName().trim().length() < 2) errors.put("first_name", "First name required, min 2 chars");
        if (req.getLastName() == null || req.getLastName().trim().length() < 2) errors.put("last_name", "Last name required, min 2 chars");
        // Uniqueness
        if (patientRepository.findByEmail(email).isPresent()) errors.put("email", "Email is already registered");
        if (patientRepository.findByPhoneNumber(phone).isPresent()) errors.put("phone_number", "Phone number is already registered");
        if (!errors.isEmpty()) {
            return new PatientRegistrationResponse(false, "Validation failed", null, null);
        }
        // Hash password
        String passwordHash = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt(12));
        // Build entity
        Patient patient = Patient.builder()
                .id(UUID.randomUUID().toString())
                .firstName(req.getFirstName().trim())
                .lastName(req.getLastName().trim())
                .email(email)
                .phoneNumber(phone)
                .passwordHash(passwordHash)
                .dateOfBirth(req.getDateOfBirth())
                .gender(Patient.Gender.valueOf(gender.toUpperCase()))
                .address(mapAddress(req.getAddress()))
                .emergencyContact(mapEmergencyContact(req.getEmergencyContact()))
                .medicalHistory(req.getMedicalHistory())
                .insuranceInfo(mapInsuranceInfo(req.getInsuranceInfo()))
                .emailVerified(false)
                .phoneVerified(false)
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        patientRepository.save(patient);
        PatientRegistrationResponse.Data data = new PatientRegistrationResponse.Data(
                patient.getId(), patient.getEmail(), patient.getPhoneNumber(), false, false
        );
        return new PatientRegistrationResponse(true, "Patient registered successfully. Verification email sent.", data, null);
    }

    private Patient.Address mapAddress(PatientRegistrationRequest.Address reqAddr) {
        if (reqAddr == null) return null;
        Patient.Address addr = new Patient.Address();
        addr.setStreet(reqAddr.getStreet().trim());
        addr.setCity(reqAddr.getCity().trim());
        addr.setState(reqAddr.getState().trim());
        addr.setZip(reqAddr.getZip().trim());
        return addr;
    }
    private Patient.EmergencyContact mapEmergencyContact(PatientRegistrationRequest.EmergencyContact reqEC) {
        if (reqEC == null) return null;
        Patient.EmergencyContact ec = new Patient.EmergencyContact();
        ec.setName(reqEC.getName());
        ec.setPhone(reqEC.getPhone());
        ec.setRelationship(reqEC.getRelationship());
        return ec;
    }
    private Patient.InsuranceInfo mapInsuranceInfo(PatientRegistrationRequest.InsuranceInfo reqII) {
        if (reqII == null) return null;
        Patient.InsuranceInfo ii = new Patient.InsuranceInfo();
        ii.setProvider(reqII.getProvider());
        ii.setPolicyNumber(reqII.getPolicyNumber());
        return ii;
    }
} 