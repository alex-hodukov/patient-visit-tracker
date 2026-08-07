package com.clinicpulse.tracker.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "visits")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "start_datetime_utc", nullable = false)
    private LocalDateTime startDateTimeUtc;

    @Column(name = "end_datetime_utc", nullable = false)
    private LocalDateTime endDateTimeUtc;

    protected Visit() {

    }

    public Visit(Patient patient, Doctor doctor, LocalDateTime startDateTimeUtc, LocalDateTime endDateTimeUtc) {
        this.patient = patient;
        this.doctor = doctor;
        this.startDateTimeUtc = startDateTimeUtc;
        this.endDateTimeUtc = endDateTimeUtc;
    }

    public Long getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalDateTime getStartDateTimeUtc() {
        return startDateTimeUtc;
    }

    public LocalDateTime getEndDateTimeUtc() {
        return endDateTimeUtc;
    }
}
