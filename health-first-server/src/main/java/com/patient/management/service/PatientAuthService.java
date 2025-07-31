package com.patient.management.service;

import com.patient.management.dto.PatientLoginRequest;
import com.patient.management.dto.PatientLoginResponse;
import com.patient.management.entity.Patient;
import com.patient.management.repository.PatientRepository;
import com.patient.management.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientAuthService {
    private final PatientRepository patientRepository;
    private final JwtUtil jwtUtil;

    public PatientLoginResponse login(PatientLoginRequest req) {
        // Validate input
        if (req.getEmail() == null || req.getEmail().isBlank() || req.getPassword() == null || req.getPassword().isBlank()) {
            return new PatientLoginResponse(false, "Email and password are required", null, "INVALID_INPUT");
        }
        Optional<Patient> patientOpt = patientRepository.findByEmail(req.getEmail().trim().toLowerCase());
        if (patientOpt.isEmpty()) {
            return new PatientLoginResponse(false, "Invalid credentials", null, "INVALID_CREDENTIALS");
        }
        Patient patient = patientOpt.get();
        if (!BCrypt.checkpw(req.getPassword(), patient.getPasswordHash())) {
            return new PatientLoginResponse(false, "Invalid credentials", null, "INVALID_CREDENTIALS");
        }
        // JWT generation
        Map<String, Object> claims = new HashMap<>();
        claims.put("patient_id", patient.getId());
        claims.put("email", patient.getEmail());
        claims.put("role", "PATIENT");
        String token = jwtUtil.generateToken(claims, 30 * 60 * 1000); // 30 min
        PatientLoginResponse.PatientInfo info = new PatientLoginResponse.PatientInfo(
            patient.getId(), patient.getFirstName(), patient.getLastName(), patient.getEmail(),
            patient.getPhoneNumber(), patient.getDateOfBirth() != null ? patient.getDateOfBirth().toString() : null,
            patient.isEmailVerified(), patient.isPhoneVerified(), patient.isActive()
        );
        PatientLoginResponse.Data data = new PatientLoginResponse.Data(token, 1800, "Bearer", info);
        return new PatientLoginResponse(true, "Login successful", data, null);
    }
} 