package com.patient.management.repository;

import com.patient.management.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByPhoneNumber(String phoneNumber);
} 