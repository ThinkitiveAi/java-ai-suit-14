package com.patient.management.controller;

import com.patient.management.dto.BookAppointmentRequest;
import com.patient.management.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AppointmentController {
    private final AvailabilityService availabilityService;

    @PostMapping("/appointments/book")
    public ResponseEntity<Map<String, Object>> book(@Valid @RequestBody BookAppointmentRequest request) {
        String ref = availabilityService.bookAppointment(request);
        return ResponseEntity.status(201).body(Map.of(
                "success", true,
                "booking_reference", ref
        ));
    }
} 