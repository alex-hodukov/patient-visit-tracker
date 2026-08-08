INSERT INTO patients (id, first_name, last_name)
VALUES
    (1, 'Bob', 'Smith'),
    (2, 'Anna', 'Taylor'),
    (3, 'John', 'Brown'),
    (4, 'Emily', 'Wilson'),
    (5, 'Michael', 'Smith');

INSERT INTO doctors (id, first_name, last_name, timezone)
VALUES
    (1, 'Joseph', 'Miller', 'Europe/Kyiv'),
    (2, 'Olivia', 'Davis', 'Europe/Warsaw'),
    (3, 'Daniel', 'Anderson', 'America/New_York');

INSERT INTO visits (
    id,
    patient_id,
    doctor_id,
    start_datetime_utc,
    end_datetime_utc
)
VALUES
    -- Bob visited Joseph several times
    (1, 1, 1, '2026-07-01 07:00:00', '2026-07-01 07:30:00'),
    (2, 1, 1, '2026-07-15 08:00:00', '2026-07-15 08:45:00'),

    -- Bob also visited Olivia
    (3, 1, 2, '2026-07-20 10:00:00', '2026-07-20 10:30:00'),

    -- Anna visited Joseph
    (4, 2, 1, '2026-07-22 11:00:00', '2026-07-22 11:30:00'),

    -- John visited Olivia
    (5, 3, 2, '2026-07-25 09:00:00', '2026-07-25 10:00:00'),

    -- Emily visited Daniel
    (6, 4, 3, '2026-07-28 15:00:00', '2026-07-28 15:30:00'),

    -- Michael visited both Joseph and Daniel
    (7, 5, 1, '2026-07-30 12:00:00', '2026-07-30 12:30:00'),
    (8, 5, 3, '2026-08-01 16:00:00', '2026-08-01 16:45:00');

ALTER TABLE patients AUTO_INCREMENT = 200;
ALTER TABLE doctors AUTO_INCREMENT = 200;
ALTER TABLE visits AUTO_INCREMENT = 200;