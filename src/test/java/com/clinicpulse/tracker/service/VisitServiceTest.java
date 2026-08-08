package com.clinicpulse.tracker.service;

import com.clinicpulse.tracker.dto.CreateVisitRequest;
import com.clinicpulse.tracker.dto.CreateVisitResponse;
import com.clinicpulse.tracker.entity.Doctor;
import com.clinicpulse.tracker.entity.Patient;
import com.clinicpulse.tracker.entity.Visit;
import com.clinicpulse.tracker.exception.DoctorNotFoundException;
import com.clinicpulse.tracker.exception.InvalidVisitIntervalException;
import com.clinicpulse.tracker.exception.PatientNotFoundException;
import com.clinicpulse.tracker.exception.VisitOverlapException;
import com.clinicpulse.tracker.repository.DoctorRepository;
import com.clinicpulse.tracker.repository.PatientRepository;
import com.clinicpulse.tracker.repository.VisitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitService visitService;

    @Test
    void shouldCreateVisit() {
        Patient patient = mock(Patient.class);
        Doctor doctor = mock(Doctor.class);
        Visit savedVisit = mock(Visit.class);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(doctor));

        when(doctor.getId()).thenReturn(2L);
        when(doctor.getTimezone()).thenReturn("Europe/Kyiv");

        when(visitRepository.existsOverlappingVisit(
                2L,
                LocalDateTime.of(2026, 8, 10, 12, 0),
                LocalDateTime.of(2026, 8, 10, 12, 45)
        )).thenReturn(false);

        when(savedVisit.getId()).thenReturn(200L);
        when(visitRepository.save(any(Visit.class))).thenReturn(savedVisit);

        CreateVisitRequest request = new CreateVisitRequest(
                LocalDateTime.of(2026, 8, 10, 15, 0),
                LocalDateTime.of(2026, 8, 10, 15, 45),
                1L,
                2L
        );

        CreateVisitResponse response = visitService.createVisit(request);

        assertEquals(200L, response.id());

        ArgumentCaptor<Visit> visitCaptor = ArgumentCaptor.forClass(Visit.class);

        verify(visitRepository).save(visitCaptor.capture());

        Visit visit = visitCaptor.getValue();

        assertSame(patient, visit.getPatient());
        assertSame(doctor, visit.getDoctor());

        assertEquals(
                LocalDateTime.of(2026, 8, 10, 12, 0),
                visit.getStartDateTimeUtc()
        );

        assertEquals(
                LocalDateTime.of(2026, 8, 10, 12, 45),
                visit.getEndDateTimeUtc()
        );
    }

    @Test
    void shouldRejectVisitWhenStartEqualsEnd() {
        CreateVisitRequest request = new CreateVisitRequest(
                LocalDateTime.of(2026, 8, 10, 15, 0),
                LocalDateTime.of(2026, 8, 10, 15, 0),
                1L,
                2L
        );

        assertThrows(InvalidVisitIntervalException.class,() -> visitService.createVisit(request));

        verifyNoInteractions(patientRepository, doctorRepository, visitRepository);
    }

    @Test
    void shouldRejectVisitWhenStartIsAfterEnd() {
        CreateVisitRequest request = new CreateVisitRequest(
                LocalDateTime.of(2026, 8, 10, 16, 0),
                LocalDateTime.of(2026, 8, 10, 15, 0),
                1L,
                2L
        );

        assertThrows(
                InvalidVisitIntervalException.class,
                () -> visitService.createVisit(request)
        );

        verifyNoInteractions(patientRepository, doctorRepository, visitRepository);
    }

    @Test
    void shouldThrowWhenPatientDoesNotExist() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        CreateVisitRequest request = validRequest();

        assertThrows(
                PatientNotFoundException.class,
                () -> visitService.createVisit(request)
        );

        verifyNoInteractions(doctorRepository, visitRepository);
    }

    @Test
    void shouldThrowWhenDoctorDoesNotExist() {
        Patient patient = mock(Patient.class);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findByIdForUpdate(2L)).thenReturn(Optional.empty());

        assertThrows(
                DoctorNotFoundException.class,
                () -> visitService.createVisit(validRequest())
        );

        verifyNoInteractions(visitRepository);
    }

    @Test
    void shouldRejectOverlappingVisit() {
        Patient patient = mock(Patient.class);
        Doctor doctor = mock(Doctor.class);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(doctor));

        when(doctor.getId()).thenReturn(2L);
        when(doctor.getTimezone()).thenReturn("Europe/Kyiv");

        when(visitRepository.existsOverlappingVisit(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(true);

        assertThrows(
                VisitOverlapException.class,
                () -> visitService.createVisit(validRequest())
        );

        verify(visitRepository, never()).save(any());
    }

    private CreateVisitRequest validRequest() {
        return new CreateVisitRequest(
                LocalDateTime.of(2026, 8, 10, 15, 0),
                LocalDateTime.of(2026, 8, 10, 15, 45),
                1L,
                2L
        );
    }
}