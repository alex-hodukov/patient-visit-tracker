package com.clinicpulse.tracker.repository;

import com.clinicpulse.tracker.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
