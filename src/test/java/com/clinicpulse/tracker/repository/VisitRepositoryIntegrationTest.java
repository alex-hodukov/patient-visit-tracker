package com.clinicpulse.tracker.repository;

import com.clinicpulse.tracker.entity.Doctor;
import com.clinicpulse.tracker.entity.Patient;
import com.clinicpulse.tracker.entity.Visit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
class VisitRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4.10");

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private VisitRepository visitRepository;

    private Doctor doctor;
    private Patient patient;

    @BeforeEach
    void setUp() {
        visitRepository.deleteAll();
        patientRepository.deleteAll();
        doctorRepository.deleteAll();

        patient = patientRepository.save(new Patient("Bob", "Smith"));
        doctor = doctorRepository.save(new Doctor("Joseph","Miller","Europe/Kyiv"));

        visitRepository.save(
                new Visit(
                        patient,
                        doctor,
                        LocalDateTime.of(2026, 8, 10, 10, 0),
                        LocalDateTime.of(2026, 8, 10, 11, 0)
                )
        );

        visitRepository.flush();
    }

    @Test
    void shouldDetectOverlapInsideExistingVisit() {
        boolean exists = visitRepository.existsOverlappingVisit(
                doctor.getId(),
                LocalDateTime.of(2026, 8, 10, 10, 30),
                LocalDateTime.of(2026, 8, 10, 10, 45)
        );

        assertTrue(exists);
    }

    @Test
    void shouldDetectOverlapFromLeftSide() {
        boolean exists = visitRepository.existsOverlappingVisit(
                doctor.getId(),
                LocalDateTime.of(2026, 8, 10, 9, 30),
                LocalDateTime.of(2026, 8, 10, 10, 30)
        );

        assertTrue(exists);
    }

    @Test
    void shouldDetectOverlapFromRightSide() {
        boolean exists = visitRepository.existsOverlappingVisit(
                doctor.getId(),
                LocalDateTime.of(2026, 8, 10, 10, 30),
                LocalDateTime.of(2026, 8, 10, 11, 30)
        );

        assertTrue(exists);
    }

    @Test
    void shouldDetectVisitContainingExistingVisit() {
        boolean exists = visitRepository.existsOverlappingVisit(
                doctor.getId(),
                LocalDateTime.of(2026, 8, 10, 9, 0),
                LocalDateTime.of(2026, 8, 10, 12, 0)
        );

        assertTrue(exists);
    }

    @Test
    void shouldAllowVisitStartingWhenPreviousVisitEnds() {
        boolean exists = visitRepository.existsOverlappingVisit(
                doctor.getId(),
                LocalDateTime.of(2026, 8, 10, 11, 0),
                LocalDateTime.of(2026, 8, 10, 12, 0)
        );

        assertFalse(exists);
    }

    @Test
    void shouldAllowVisitEndingWhenNextVisitStarts() {
        boolean exists = visitRepository.existsOverlappingVisit(
                doctor.getId(),
                LocalDateTime.of(2026, 8, 10, 9, 0),
                LocalDateTime.of(2026, 8, 10, 10, 0)
        );

        assertFalse(exists);
    }

    @Test
    void shouldIgnoreVisitsOfAnotherDoctor() {
        Doctor anotherDoctor = doctorRepository.save(
                new Doctor("Anna","Brown","Europe/Warsaw"));

        boolean exists = visitRepository.existsOverlappingVisit(
                anotherDoctor.getId(),
                LocalDateTime.of(2026, 8, 10, 10, 30),
                LocalDateTime.of(2026, 8, 10, 10, 45)
        );

        assertFalse(exists);
    }
}