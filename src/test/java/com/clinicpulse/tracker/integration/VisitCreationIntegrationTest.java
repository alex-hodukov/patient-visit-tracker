package com.clinicpulse.tracker.integration;

import com.clinicpulse.tracker.entity.Doctor;
import com.clinicpulse.tracker.entity.Patient;
import com.clinicpulse.tracker.entity.Visit;
import com.clinicpulse.tracker.repository.DoctorRepository;
import com.clinicpulse.tracker.repository.PatientRepository;
import com.clinicpulse.tracker.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class VisitCreationIntegrationTest {

    @Container
    @ServiceConnection
    static final MySQLContainer mysql = new MySQLContainer("mysql:8.4.10");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private VisitRepository visitRepository;

    private Patient patient;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        visitRepository.deleteAll();
        patientRepository.deleteAll();
        doctorRepository.deleteAll();

        patient = patientRepository.save(new Patient("Bob", "Smith"));
        doctor = doctorRepository.save(new Doctor("Joseph", "Miller", "Europe/Kyiv"));
    }

    @Test
    void shouldCreateVisitAndStoreUtcTime() throws Exception {
        final var content = """
                            {
                              "start": "2026-08-10 15:00",
                              "end": "2026-08-10 15:45",
                              "patientId": %d,
                              "doctorId": %d
                            }
                            """;

        mockMvc.perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content.formatted(patient.getId(),doctor.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber());

        List<Visit> visits = visitRepository.findAll();

        assertEquals(1, visits.size());

        Visit visit = visits.getFirst();

        assertEquals(
                LocalDateTime.of(2026, 8, 10, 12, 0),
                visit.getStartDateTimeUtc()
        );

        assertEquals(
                LocalDateTime.of(2026, 8, 10, 12, 45),
                visit.getEndDateTimeUtc()
        );

        assertEquals(patient.getId(), visit.getPatient().getId());
        assertEquals(doctor.getId(), visit.getDoctor().getId());
    }

    @Test
    void shouldRejectOverlappingVisit() throws Exception {
        visitRepository.saveAndFlush(
                new Visit(
                        patient,
                        doctor,
                        LocalDateTime.of(2026, 8, 10, 12, 0),
                        LocalDateTime.of(2026, 8, 10, 13, 0)
                )
        );

        final var content = """
                            {
                              "start": "2026-08-10 15:30",
                              "end": "2026-08-10 16:30",
                              "patientId": %d,
                              "doctorId": %d
                            }
                            """;

        mockMvc.perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content.formatted(patient.getId(), doctor.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title")
                        .value("Visit conflict"));

        assertEquals(1, visitRepository.count());
    }
}