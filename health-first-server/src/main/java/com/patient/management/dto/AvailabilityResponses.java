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
        private DateRangeDto date_range;
        private int total_appointments_available;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderAvailabilityData {
        private String provider_id;
        private AvailabilitySummaryDto availability_summary;
        private java.util.List<DayAvailabilityDto> availability;
    }
} 