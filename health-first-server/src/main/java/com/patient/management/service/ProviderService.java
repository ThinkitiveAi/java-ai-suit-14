package com.patient.management.service;

import com.patient.management.dto.*;
import com.patient.management.entity.Patient;
import com.patient.management.entity.Provider;
import com.patient.management.repository.ProviderRepository;
import com.patient.management.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
public class ProviderService {
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private ValidationService validationService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private final JwtUtil jwtUtil;


    @Transactional
    public ProviderRegistrationResponse registerProvider(ProviderRegistrationRequest request) {
        // Trim and normalize input
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null;
        String phone = request.getPhoneNumber() != null ? request.getPhoneNumber().trim() : null;
        String license = request.getLicenseNumber() != null ? request.getLicenseNumber().trim() : null;
        String specialization = request.getSpecialization() != null ? request.getSpecialization().trim() : null;

        // Validation
        StringBuilder errors = new StringBuilder();
        if (!validationService.isValidEmail(email)) errors.append("Invalid email format. ");
        if (!validationService.isValidPhone(phone)) errors.append("Invalid phone number format. ");
        if (!validationService.isValidPassword(request.getPassword())) errors.append("Password must be 8+ chars, upper, lower, number, special char. ");
        if (!request.getPassword().equals(request.getConfirmPassword())) errors.append("Passwords do not match. ");
        if (!validationService.isValidLicense(license)) errors.append("License number must be alphanumeric. ");
        if (!validationService.isValidSpecialization(specialization)) errors.append("Invalid specialization. ");
        if (request.getClinicAddress() == null) errors.append("Clinic address required. ");
        else {
            if (request.getClinicAddress().getStreet() == null || request.getClinicAddress().getStreet().trim().isEmpty()) errors.append("Street required. ");
            if (request.getClinicAddress().getCity() == null || request.getClinicAddress().getCity().trim().isEmpty()) errors.append("City required. ");
            if (request.getClinicAddress().getState() == null || request.getClinicAddress().getState().trim().isEmpty()) errors.append("State required. ");
            if (request.getClinicAddress().getZip() == null || request.getClinicAddress().getZip().trim().isEmpty()) errors.append("Zip required. ");
        }
        if (errors.length() > 0) {
            return new ProviderRegistrationResponse(false, errors.toString().trim(), null);
        }

        // Uniqueness checks
        if (providerRepository.findByEmail(email).isPresent()) {
            return new ProviderRegistrationResponse(false, "Email already registered.", null);
        }
        if (providerRepository.findByPhoneNumber(phone).isPresent()) {
            return new ProviderRegistrationResponse(false, "Phone number already registered.", null);
        }
        if (providerRepository.findByLicenseNumber(license).isPresent()) {
            return new ProviderRegistrationResponse(false, "License number already registered.", null);
        }

        // Hash password
        String passwordHash = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(12));

        // Create provider entity
        Provider provider = Provider.builder()
                .id(UUID.randomUUID().toString())
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(email)
                .phoneNumber(phone)
                .passwordHash(passwordHash)
                .specialization(specialization)
                .licenseNumber(license)
                .yearsOfExperience(request.getYearsOfExperience())
                .clinicAddress(mapClinicAddress(request.getClinicAddress()))
                .verificationStatus(Provider.VerificationStatus.PENDING)
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        try {
            providerRepository.save(provider);
        } catch (DataIntegrityViolationException e) {
            return new ProviderRegistrationResponse(false, "Duplicate entry detected.", null);
        } catch (Exception e) {
            return new ProviderRegistrationResponse(false, "Internal server error.", null);
        }

        // Send verification email (stub)
        String verificationToken = UUID.randomUUID().toString();
        emailService.sendVerificationEmail(email, verificationToken);

        // Success response
        ProviderRegistrationResponse.Data data = new ProviderRegistrationResponse.Data(
                provider.getId(), provider.getEmail(), provider.getVerificationStatus().name().toLowerCase()
        );
        return new ProviderRegistrationResponse(true, "Provider registered successfully. Verification email sent.", data);
    }

    private Provider.ClinicAddress mapClinicAddress(ProviderRegistrationRequest.ClinicAddress reqAddr) {
        Provider.ClinicAddress addr = new Provider.ClinicAddress();
        addr.setStreet(reqAddr.getStreet().trim());
        addr.setCity(reqAddr.getCity().trim());
        addr.setState(reqAddr.getState().trim());
        addr.setZip(reqAddr.getZip().trim());
        return addr;
    }


    public ProviderLoginResponse login(ProviderLoginRequest req) {
        // Validate input
        if (req.getIdentifier() == null || req.getIdentifier().isBlank() || req.getPassword() == null || req.getPassword().isBlank()) {
            return new ProviderLoginResponse(false, "Email and password are required", null, "INVALID_INPUT");
        }
        Optional<Provider> patientOpt = providerRepository.findByEmail(req.getIdentifier().trim().toLowerCase());
        if (patientOpt.isEmpty()) {
            return new ProviderLoginResponse(false, "Invalid credentials", null, "INVALID_CREDENTIALS");
        }
        Provider provider = patientOpt.get();
        if (!BCrypt.checkpw(req.getPassword(), provider.getPasswordHash())) {
            return new ProviderLoginResponse(false, "Invalid credentials", null, "INVALID_CREDENTIALS");
        }
        // JWT generation
        Map<String, Object> claims = new HashMap<>();
        claims.put("patient_id", provider.getId());
        claims.put("email", provider.getEmail());
        claims.put("role", "PATIENT");
        String token = jwtUtil.generateToken(claims, 30 * 60 * 1000); // 30 min
        ProviderLoginResponse.ProviderInfo info = new ProviderLoginResponse.ProviderInfo(
                provider.getId(), provider.getFirstName(), provider.getLastName(), provider.getEmail(),
                provider.getSpecialization(), provider.getVerificationStatus().toString(), provider.isActive()
        );
        ProviderLoginResponse.Data data = new ProviderLoginResponse.Data(token, 1800, "Bearer", info);
        return new ProviderLoginResponse(true, "Login successful", data, null);
    }
} 