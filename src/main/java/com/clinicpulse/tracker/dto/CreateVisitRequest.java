package com.clinicpulse.tracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CreateVisitRequest(
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime start,

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime end,

        @NotNull
        @Positive
        Long patientId,

        @NotNull
        @Positive
        Long doctorId
) {
}
