package com.clinicpulse.tracker.exception;

public class DoctorNotFoundException extends RuntimeException {

    public DoctorNotFoundException(Long doctorId) {
        super("Doctor with id %d was not found".formatted(doctorId));
    }
}
