package com.clinicpulse.tracker.exception;

public class InvalidVisitIntervalException extends RuntimeException {

    public InvalidVisitIntervalException() {
        super("Visit start time must be earlier than end time");
    }
}
