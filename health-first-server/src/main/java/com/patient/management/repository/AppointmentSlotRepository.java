package com.patient.management.repository;

import com.patient.management.entity.AppointmentSlot;
import com.patient.management.entity.ProviderAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, String> {
    @Query("SELECT s FROM AppointmentSlot s WHERE s.providerId = :providerId AND s.slotStartTime < :end AND s.slotEndTime > :start AND s.status <> 'CANCELLED'")
    List<AppointmentSlot> findOverlappingSlots(@Param("providerId") String providerId,
                                               @Param("start") Instant start,
                                               @Param("end") Instant end);

    @Query("SELECT s FROM AppointmentSlot s WHERE s.providerId = :providerId AND s.slotStartTime >= :start AND s.slotEndTime <= :end AND (:status IS NULL OR s.status = :status) AND (:type IS NULL OR s.appointmentType = :type)")
    List<AppointmentSlot> findByProviderAndRangeAndFilters(@Param("providerId") String providerId,
                                                           @Param("start") Instant start,
                                                           @Param("end") Instant end,
                                                           @Param("status") AppointmentSlot.SlotStatus status,
                                                           @Param("type") ProviderAvailability.AppointmentType type);

    @Query("SELECT s FROM AppointmentSlot s WHERE s.slotStartTime >= :start AND s.slotEndTime <= :end AND (:statusOnlyAvailable = false OR s.status = 'AVAILABLE')")
    List<AppointmentSlot> findInRange(@Param("start") Instant start,
                                      @Param("end") Instant end,
                                      @Param("statusOnlyAvailable") boolean statusOnlyAvailable);

    List<AppointmentSlot> findByAvailabilityId(String availabilityId);
} 