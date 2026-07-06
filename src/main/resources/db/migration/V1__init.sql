CREATE TABLE drivers (
    id          VARCHAR(36)  PRIMARY KEY,
    full_name   VARCHAR(128) NOT NULL,
    email       VARCHAR(128) NOT NULL
);

CREATE TABLE vehicles (
    id            VARCHAR(36) PRIMARY KEY,
    license_plate VARCHAR(32) NOT NULL,
    model         VARCHAR(64) NOT NULL,
    vin           VARCHAR(32) NOT NULL UNIQUE,
    driver_id     VARCHAR(36) REFERENCES drivers (id)
);

CREATE INDEX idx_vehicles_license_plate ON vehicles (license_plate);

INSERT INTO drivers (id, full_name, email) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Driver One',   'driver1@example.com'),
    ('22222222-2222-2222-2222-222222222222', 'Driver Two',   'driver2@example.com'),
    ('33333333-3333-3333-3333-333333333333', 'Driver Three', 'driver3@example.com');

INSERT INTO vehicles (id, license_plate, model, vin, driver_id) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'B-PG-1001', 'Mercedes Actros', 'TESTVIN0000000001', '11111111-1111-1111-1111-111111111111'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'B-PG-1002', 'MAN TGX',         'TESTVIN0000000002', '22222222-2222-2222-2222-222222222222'),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'B-PG-1003', 'Volvo FH',        'TESTVIN0000000003', '33333333-3333-3333-3333-333333333333');
