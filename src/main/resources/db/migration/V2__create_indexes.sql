CREATE INDEX idx_visits_doctor_interval
    ON visits (
        doctor_id,
        start_datetime_utc,
        end_datetime_utc
    );

CREATE INDEX idx_visits_patient_doctor_start
    ON visits (
        patient_id,
        doctor_id,
        start_datetime_utc DESC
    );

CREATE INDEX idx_visits_doctor_patient
    ON visits (
        doctor_id,
        patient_id
    );