package com.clinicpulse.tracker.exception;

import com.clinicpulse.tracker.entity.Patient;

public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(Long patientId) {
        super("Patient with id %d was not found".formatted(patientId));
    }
}
