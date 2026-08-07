CREATE TABLE patients (
    id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE doctors (
    id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    timezone VARCHAR(64) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE visits (
    id BIGINT NOT NULL AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    start_datetime_utc DATETIME NOT NULL,
    end_datetime_utc DATETIME NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_visits_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients (id),

    CONSTRAINT fk_visits_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES doctors (id)
);