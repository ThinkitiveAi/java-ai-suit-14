package com.patient.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchAvailabilityResponse {
    private boolean success;
    private DataPayload data;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataPayload {
        private SearchCriteria search_criteria;
        private int total_results;
        private List<Result> results;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchCriteria {
        private String date;
        private String start_date;
        private String end_date;
        private String specialization;
        private String location;
        private String appointment_type;
        private Boolean insurance_accepted;
        private BigDecimal max_price;
        private String timezone;
        private Boolean available_only;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private ProviderInfo provider;
        private List<AvailableSlot> available_slots;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderInfo {
        private String id;
        private String name;
        private String specialization;
        private int years_of_experience;
        private double rating;
        private String clinic_address;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableSlot {
        private String slot_id;
        private String date;
        private String start_time;
        private String end_time;
        private String appointment_type;
        private Location location;
        private Pricing pricing;
        private List<String> special_requirements;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private String type;
        private String address;
        private String room_number;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pricing {
        private BigDecimal base_fee;
        private Boolean insurance_accepted;
        private String currency;
    }
} 