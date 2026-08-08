package com.clinicpulse.tracker.controller;

import com.clinicpulse.tracker.dto.CreateVisitResponse;
import com.clinicpulse.tracker.exception.DoctorNotFoundException;
import com.clinicpulse.tracker.exception.VisitOverlapException;
import com.clinicpulse.tracker.service.VisitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitController.class)
class VisitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VisitService visitService;

    @Test
    void shouldCreateVisit() throws Exception {
        final var content = """
                            {
                              "start": "2026-08-10 15:00",
                              "end": "2026-08-10 15:45",
                              "patientId": 1,
                              "doctorId": 2
                            }
                            """;

        when(visitService.createVisit(any())).thenReturn(new CreateVisitResponse(200L));

        mockMvc.perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(200));
    }

    @Test
    void shouldReturnBadRequestWhenPatientIdIsNegative() throws Exception {
        final var content = """
                            {
                              "start": "2026-08-10 15:00",
                              "end": "2026-08-10 15:45",
                              "patientId": -1,
                              "doctorId": 2
                            }
                            """;

        mockMvc.perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.errors[0].field")
                        .value("patientId"));
    }

    @Test
    void shouldReturnBadRequestWhenRequiredFieldIsMissing() throws Exception {
        final var content = """
                            {
                              "start": "2026-08-10 15:00",
                              "end": "2026-08-10 15:45",
                              "patientId": 1
                            }
                            """;
        mockMvc.perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Validation failed"));
    }

    @Test
    void shouldReturnNotFoundWhenDoctorDoesNotExist() throws Exception {
        when(visitService.createVisit(any()))
                .thenThrow(new DoctorNotFoundException(999L));

        final var content = """
                            {
                              "start": "2026-08-10 15:00",
                              "end": "2026-08-10 15:45",
                              "patientId": 1,
                              "doctorId": 999
                            }
                            """;
        mockMvc.perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title")
                        .value("Doctor not found"))
                .andExpect(jsonPath("$.detail")
                        .value("Doctor with id 999 was not found"));
    }

    @Test
    void shouldReturnConflictForOverlappingVisit() throws Exception {
        when(visitService.createVisit(any()))
                .thenThrow(new VisitOverlapException());

        final var content = """
                            {
                              "start": "2026-08-10 15:00",
                              "end": "2026-08-10 15:45",
                              "patientId": 1,
                              "doctorId": 2
                            }
                            """;
        mockMvc.perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title")
                        .value("Visit conflict"));
    }

    @Test
    void shouldReturnBadRequestForInvalidDateFormat() throws Exception {
        final var content = """
                            {
                              "start": "10 August 2026",
                              "end": "2026-08-10 15:45",
                              "patientId": 1,
                              "doctorId": 2
                            }
                            """;
        mockMvc.perform(post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Invalid request"));
    }
}
