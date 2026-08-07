package com.clinicpulse.tracker.repository;

import com.clinicpulse.tracker.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    @Query(value = """
           SELECT EXISTS (
                SELECT 1
                FROM visits
                WHERE doctor_id = :doctorId
                  AND start_datetime_utc < :end
                  AND end_datetime_utc > :start
            )
            """, nativeQuery = true)
    boolean existsOverlappingVisit(
            @Param("id") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
