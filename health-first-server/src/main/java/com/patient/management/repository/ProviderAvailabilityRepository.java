package com.patient.management.repository;

import com.patient.management.entity.ProviderAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProviderAvailabilityRepository extends JpaRepository<ProviderAvailability, String> {
    List<ProviderAvailability> findByProviderIdAndDateBetween(String providerId, LocalDate start, LocalDate end);

    @Query("SELECT a FROM ProviderAvailability a WHERE a.providerId = :providerId AND a.date BETWEEN :start AND :end AND (:status IS NULL OR a.status = :status) AND (:type IS NULL OR a.appointmentType = :type)")
    List<ProviderAvailability> searchByProviderAndDateRange(
            @Param("providerId") String providerId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("status") ProviderAvailability.AvailabilityStatus status,
            @Param("type") ProviderAvailability.AppointmentType type
    );
} 