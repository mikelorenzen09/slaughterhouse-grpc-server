-- Run this script against the slaughterhouse database before starting the application.
-- It also upgrades an existing Part 2 schema without replacing its records.
BEGIN;

CREATE TABLE IF NOT EXISTS animal (
    registration_number VARCHAR(255) PRIMARY KEY,
    species VARCHAR(255),
    weight_kg NUMERIC NOT NULL CHECK (weight_kg > 0),
    arrival_date DATE NOT NULL,
    origin VARCHAR(255)
);

-- Older animals can have an unknown origin; new REST registrations require one.
ALTER TABLE animal ADD COLUMN IF NOT EXISTS origin VARCHAR(255);

CREATE TABLE IF NOT EXISTS tray (
    tray_id SERIAL PRIMARY KEY,
    part_type VARCHAR(255) NOT NULL,
    max_capacity_kg NUMERIC NOT NULL CHECK (max_capacity_kg > 0)
);

CREATE TABLE IF NOT EXISTS part (
    part_id SERIAL PRIMARY KEY,
    animal_registration_number VARCHAR(255) NOT NULL REFERENCES animal(registration_number),
    part_type VARCHAR(255) NOT NULL,
    weight_kg NUMERIC NOT NULL CHECK (weight_kg > 0),
    tray_id INTEGER NOT NULL REFERENCES tray(tray_id)
);

CREATE TABLE IF NOT EXISTS product (
    product_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    product_type VARCHAR(255) NOT NULL,
    packaged_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS product_tray (
    product_id INTEGER NOT NULL REFERENCES product(product_id),
    tray_id INTEGER NOT NULL REFERENCES tray(tray_id),
    PRIMARY KEY (product_id, tray_id)
);

COMMIT;
