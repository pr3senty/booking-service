CREATE TYPE role_enum AS ENUM ('GUEST', 'STUDENT', 'STAFF', 'ADMIN');

CREATE TABLE coworkings (
    id SERIAL PRIMARY KEY,
    floor INTEGER NOT NULL,
    role_required role_enum NOT NULL,
    label VARCHAR(255) NOT NULL,
    occupancy INTEGER NOT NULL
);

INSERT INTO coworkings (floor, role_required, label, occupancy) VALUES
        (1, 'GUEST', '101', 5),
        (1, 'STUDENT', '102', 10),
        (1, 'STAFF', '103', 8),
        (2, 'GUEST', '201', 6),
        (2, 'STUDENT', '202', 12),
        (2, 'STAFF', '203', 10),
        (2, 'ADMIN', '204', 15),
        (3, 'STUDENT', '301', 9),
        (3, 'STAFF', '302', 7),
        (3, 'ADMIN', '303', 20),
        (3, 'GUEST', '304', 5),
        (4, 'STUDENT', '401', 14),
        (4, 'STAFF', '402', 10),
        (4, 'ADMIN', '403', 25),
        (5, 'GUEST', '501', 6),
        (5, 'STUDENT', '502', 11),
        (5, 'STAFF', '503', 9),
        (5, 'ADMIN', '504', 18),
        (6, 'STUDENT', '601', 13),
        (6, 'STAFF', '602', 8),
        (6, 'ADMIN', '603', 22);
