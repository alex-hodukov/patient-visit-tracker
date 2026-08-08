package com.clinicpulse.tracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PatientNotFoundException.class)
    public ProblemDetail handlePatientNotFound(PatientNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "Patient not found", ex.getMessage());
    }

    public ProblemDetail handleDoctorNotFound(DoctorNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "Doctor not found", ex.getMessage());
    }

    public ProblemDetail handleInvalidVisitInterval(InvalidVisitIntervalException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid visit interval", ex.getMessage());
    }

    public ProblemDetail handleVisitOverlap(VisitOverlapException ex) {
        return problem(HttpStatus.CONFLICT, "Visit conflict", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation failed");
        problemDetail.setDetail("Request contains invalid fields");
        problemDetail.setProperty("errors",
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                        .toList()
        );

        return problemDetail;
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        var problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);

        return problemDetail;
    }

    private record ValidationError(String field, String message) {

    }
}
