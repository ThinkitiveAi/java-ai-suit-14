package com.patient.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricingDto {
    private BigDecimal base_fee;
    private Boolean insurance_accepted;
    private String currency;
} 