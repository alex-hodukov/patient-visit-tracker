package com.clinicpulse.tracker.service;

import com.clinicpulse.tracker.dto.CreateVisitRequest;
import com.clinicpulse.tracker.dto.CreateVisitResponse;
import com.clinicpulse.tracker.entity.Visit;
import com.clinicpulse.tracker.exception.DoctorNotFoundException;
import com.clinicpulse.tracker.exception.InvalidVisitIntervalException;
import com.clinicpulse.tracker.exception.PatientNotFoundException;
import com.clinicpulse.tracker.exception.VisitOverlapException;
import com.clinicpulse.tracker.repository.DoctorRepository;
import com.clinicpulse.tracker.repository.PatientRepository;
import com.clinicpulse.tracker.repository.VisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Service
public class VisitService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final VisitRepository visitRepository;

    public VisitService(PatientRepository patientRepository, DoctorRepository doctorRepository, VisitRepository visitRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.visitRepository = visitRepository;
    }

    @Transactional
    public CreateVisitResponse createVisit(CreateVisitRequest request) {
        validateInterval(request.start(), request.end());

        var patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new PatientNotFoundException(request.patientId()));

        var doctor = doctorRepository.findByIdForUpdate(request.doctorId())
                .orElseThrow(() -> new DoctorNotFoundException(request.doctorId()));

        var zoneId = ZoneId.of(doctor.getTimezone());
        var startUtc = toUtc(request.start(), zoneId);
        var endUtc = toUtc(request.end(), zoneId);

        if (visitRepository.existsOverlappingVisit(doctor.getId(), startUtc, endUtc)) {
            throw new VisitOverlapException();
        }

        var visit = new Visit(patient, doctor, startUtc, endUtc);
        var savedVisit = visitRepository.save(visit);

        return new CreateVisitResponse(savedVisit.getId());
    }

    private void validateInterval(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new InvalidVisitIntervalException();
        }
    }

    private LocalDateTime toUtc(LocalDateTime dateTime, ZoneId zoneId) {
        return dateTime
                .atZone(zoneId)
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();
    }

}
