package com.patient.management.controller;

import com.patient.management.service.ProviderService;
import com.patient.management.dto.ProviderRegistrationRequest;
import com.patient.management.dto.ProviderRegistrationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/provider")
public class ProviderController {
    @Autowired
    private ProviderService providerService;

    @PostMapping("/register")
    public ResponseEntity<ProviderRegistrationResponse> registerProvider(@Valid @RequestBody ProviderRegistrationRequest request) {
        ProviderRegistrationResponse response = providerService.registerProvider(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
} 