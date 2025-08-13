package com.patient.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponses {
    private boolean success;
    private String message;
    private Object data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateData {
        private String availability_id;
        private int slots_created;
        private DateRange date_range;
        private int total_appointments_available;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateRange {
        private String start;
        private String end;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderAvailabilityData {
        private String provider_id;
        private AvailabilitySummary availability_summary;
        private java.util.List<DayAvailability> availability;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilitySummary {
        private int total_slots;
        private int available_slots;
        private int booked_slots;
        private int cancelled_slots;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayAvailability {
        private String date;
        private java.util.List<Slot> slots;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Slot {
        private String slot_id;
        private String start_time;
        private String end_time;
        private String status;
        private String appointment_type;
        private SearchAvailabilityResponse.Location location;
        private SearchAvailabilityResponse.Pricing pricing;
    }
} 