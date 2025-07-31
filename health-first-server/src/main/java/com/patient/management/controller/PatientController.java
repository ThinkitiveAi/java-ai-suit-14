package com.patient.management.controller;

import com.patient.management.dto.PatientRegistrationRequest;
import com.patient.management.dto.PatientRegistrationResponse;
import com.patient.management.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patient")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @PostMapping("/register")
    public ResponseEntity<PatientRegistrationResponse> register(@Valid @RequestBody PatientRegistrationRequest req) {
        PatientRegistrationResponse response = patientService.register(req);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.CREATED : HttpStatus.UNPROCESSABLE_ENTITY);
    }
} 