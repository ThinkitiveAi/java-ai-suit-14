package com.patient.management.controller;

import com.patient.management.dto.PatientLoginRequest;
import com.patient.management.dto.PatientLoginResponse;
import com.patient.management.service.PatientAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patient")
@RequiredArgsConstructor
public class PatientAuthController {
    private final PatientAuthService patientAuthService;

    @PostMapping("/login")
    public ResponseEntity<PatientLoginResponse> login(@Valid @RequestBody PatientLoginRequest req) {
        PatientLoginResponse response = patientAuthService.login(req);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);
    }
} 