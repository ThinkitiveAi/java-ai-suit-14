package com.patient.management.repository;

import com.patient.management.entity.PatientSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientSessionRepository extends JpaRepository<PatientSession, String> {
    List<PatientSession> findByPatientIdAndIsRevokedFalse(String patientId);
    Optional<PatientSession> findByRefreshTokenHash(String refreshTokenHash);
} 