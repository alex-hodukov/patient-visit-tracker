package com.clinicpulse.tracker.exception;

public class VisitOverlapException extends RuntimeException {

    public VisitOverlapException() {
        super("The doctor already has a visit during the specified time");
    }
}
