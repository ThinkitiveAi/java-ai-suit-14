package com.patient.management.controller;

import com.patient.management.dto.*;
import com.patient.management.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // 1. Create Availability Slots
    @PostMapping("/provider/availability")
    public ResponseEntity<AvailabilityResponses> createAvailability(
            @RequestParam("provider_id") String providerId,
            @Valid @RequestBody CreateAvailabilityRequest request
    ) {
        AvailabilityResponses response = availabilityService.createAvailability(providerId, request);
        return ResponseEntity.status(201).body(response);
    }

    // 2. Get Provider Availability
    @GetMapping("/provider/{provider_id}/availability")
    public ResponseEntity<AvailabilityResponses> getProviderAvailability(
            @PathVariable("provider_id") String providerId,
            @RequestParam("start_date") String startDate,
            @RequestParam("end_date") String endDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "appointment_type", required = false) String appointmentType,
            @RequestParam(value = "timezone", required = false) String timezone
    ) {
        AvailabilityResponses.ProviderAvailabilityData data = availabilityService.getProviderAvailability(
                providerId,
                LocalDate.parse(startDate, DATE_FMT),
                LocalDate.parse(endDate, DATE_FMT),
                status,
                appointmentType,
                timezone
        );
        return ResponseEntity.ok(new AvailabilityResponses(true, null, data));
    }

    // 3. Update Specific Availability Slot
    @PutMapping("/provider/availability/{slot_id}")
    public ResponseEntity<Map<String, Object>> updateSlot(
            @PathVariable("slot_id") String slotId,
            @RequestParam(value = "timezone", required = false) String timezone,
            @Valid @RequestBody UpdateAvailabilitySlotRequest request
    ) {
        availabilityService.updateSlot(slotId, request, timezone);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 4. Delete Availability Slot
    @DeleteMapping("/provider/availability/{slot_id}")
    public ResponseEntity<Map<String, Object>> deleteSlot(
            @PathVariable("slot_id") String slotId,
            @RequestParam(value = "delete_recurring", required = false, defaultValue = "false") boolean deleteRecurring,
            @RequestParam(value = "reason", required = false) String reason
    ) {
        availabilityService.deleteSlot(slotId, deleteRecurring, reason);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 5. Patient Search for Available Slots
    @GetMapping("/availability/search")
    public ResponseEntity<SearchAvailabilityResponse> search(
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "start_date", required = false) String startDate,
            @RequestParam(value = "end_date", required = false) String endDate,
            @RequestParam(value = "specialization", required = false) String specialization,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "appointment_type", required = false) String appointmentType,
            @RequestParam(value = "insurance_accepted", required = false) Boolean insuranceAccepted,
            @RequestParam(value = "max_price", required = false) java.math.BigDecimal maxPrice,
            @RequestParam(value = "timezone", required = false) String timezone,
            @RequestParam(value = "available_only", required = false, defaultValue = "true") Boolean availableOnly
    ) {
        SearchAvailabilityResponse resp = availabilityService.search(date, startDate, endDate, specialization, location, appointmentType, insuranceAccepted, maxPrice, timezone, availableOnly);
        return ResponseEntity.ok(resp);
    }
} 