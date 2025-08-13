package com.patient.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchDataPayloadDto {
    private SearchCriteriaDto search_criteria;
    private int total_results;
    private List<SearchResultDto> results;
} 