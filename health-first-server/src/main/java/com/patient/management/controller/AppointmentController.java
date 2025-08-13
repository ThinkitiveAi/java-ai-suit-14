package com.patient.management.controller;

import com.patient.management.dto.BookAppointmentRequest;
import com.patient.management.dto.AppointmentListRequest;
import com.patient.management.dto.AppointmentListResponse;
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

    @GetMapping("/appointments")
    public ResponseEntity<AppointmentListResponse> listAppointments(
            @RequestParam("user_id") String userId,
            @RequestParam("user_type") String userType,
            @RequestParam("start_date") String startDate,
            @RequestParam("end_date") String endDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "appointment_type", required = false) String appointmentType,
            @RequestParam(value = "timezone", required = false) String timezone
    ) {
        AppointmentListRequest request = new AppointmentListRequest(startDate, endDate, status, appointmentType, timezone);
        AppointmentListResponse response = availabilityService.listAppointments(userId, userType, request);
        return ResponseEntity.ok(response);
    }
} 