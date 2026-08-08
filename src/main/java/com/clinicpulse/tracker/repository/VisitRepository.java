package com.clinicpulse.tracker.repository;

import com.clinicpulse.tracker.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    @Query(value = """
           SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END
           FROM Visit v
           WHERE v.doctor.id = :doctorId
                AND v.startDateTimeUtc < :end
                AND v.endDateTimeUtc > :start
           """)
    boolean existsOverlappingVisit(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
