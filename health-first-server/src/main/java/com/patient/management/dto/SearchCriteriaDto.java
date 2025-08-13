package com.patient.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteriaDto {
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