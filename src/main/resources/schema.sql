SHOW databases;

-- Create database
CREATE DATABASE IF NOT EXISTS wigell_camping_members_club;
USE wigell_camping_members_club;

-- Clean up old tables (Drop order matters due to foreign keys)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS member_history;
DROP TABLE IF EXISTS rentals;
DROP TABLE IF EXISTS profits;
DROP TABLE IF EXISTS tents;
DROP TABLE IF EXISTS gear;
DROP TABLE IF EXISTS vehicles;
DROP TABLE IF EXISTS members;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Members
CREATE TABLE IF NOT EXISTS members
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    first_name   VARCHAR(100),
    last_name    VARCHAR(100),
    level        VARCHAR(50),
    entered_date DATE
);

-- 2. Member History
CREATE TABLE IF NOT EXISTS member_history
(
    member_id         INT,
    event_description VARCHAR(255),
    FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE CASCADE
);

-- 3. Gear (Generic Gear)
CREATE TABLE IF NOT EXISTS gear
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    model     VARCHAR(100),
    type      VARCHAR(50),
    capacity  VARCHAR(50),
    cost      DECIMAL(10, 2),
    is_rented BOOLEAN DEFAULT 0
);

-- 4. Tents (New Table for Tents)
CREATE TABLE IF NOT EXISTS tents
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    model     VARCHAR(100),
    capacity  VARCHAR(50),
    cost      DECIMAL(10, 2),
    is_rented BOOLEAN DEFAULT 0
);

-- 5. Vehicles
CREATE TABLE IF NOT EXISTS vehicles
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    make      VARCHAR(50),
    model     VARCHAR(100),
    year      VARCHAR(10),
    type      VARCHAR(50),
    capacity  VARCHAR(50),
    cost      DECIMAL(10, 2),
    is_rented BOOLEAN DEFAULT 0
);

-- 6. Rentals
CREATE TABLE IF NOT EXISTS rentals
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    member_id   INT,
    item_id     INT, -- Corresponds to the ID in the specific table (vehicle/gear/tent)
    item_type   VARCHAR(50), -- 'VEHICLE', 'GEAR', or 'TENT'
    rental_date DATE,
    return_date DATE,
    rental_days INT,
    total_cost  DECIMAL(10, 2),
    status      VARCHAR(20) DEFAULT 'ACTIVE',
    FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE SET NULL
);

-- 7. Profits
CREATE TABLE IF NOT EXISTS profits
(
    id     INT PRIMARY KEY AUTO_INCREMENT,
    date   DATE,
    amount DECIMAL(10, 2)
);


USE wigell_camping_members_club;

-- ==========================================
-- CLEAR DATA & RESET AUTO-INCREMENTS
-- ==========================================
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE rentals;
TRUNCATE TABLE profits;
TRUNCATE TABLE tents;
TRUNCATE TABLE gear;
TRUNCATE TABLE vehicles;
TRUNCATE TABLE member_history;
TRUNCATE TABLE members;

-- Ensure all IDs start at 1
ALTER TABLE members AUTO_INCREMENT = 1;
ALTER TABLE vehicles AUTO_INCREMENT = 1;
ALTER TABLE gear AUTO_INCREMENT = 1;
ALTER TABLE tents AUTO_INCREMENT = 1;
ALTER TABLE rentals AUTO_INCREMENT = 1;
ALTER TABLE member_history AUTO_INCREMENT = 1;
ALTER TABLE profits AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================
-- 1. MEMBERS (IDs will be 1 - 20)
-- ==========================================
INSERT INTO members (first_name, last_name, level, entered_date)
VALUES ('Felix', 'Kjellberg', 'Premium', '2025-11-01'),      -- ID: 1
       ('Tomas', 'Wigell', 'Premium', '2025-10-15'),         -- ID: 2
       ('Stellan', 'Skarsgård', 'Student', '2025-11-20'),    -- ID: 3
       ('Kevin', 'Costner', 'Premium', '2025-12-01'),        -- ID: 4
       ('Sarah', 'Sjöström', 'Premium', '2025-09-01'),       -- ID: 5
       ('Peter', 'Forsberg', 'Standard', '2025-11-05'),      -- ID: 6
       ('Malin', 'Åkerman', 'Standard', '2025-12-10'),       -- ID: 7
       ('Kurt', 'Olsson', 'Standard', '2025-11-15'),         -- ID: 8
       ('Carola', 'Häggkvist', 'Standard', '2025-10-30'),    -- ID: 9
       ('Robyn', 'Carlsson', 'Premium', '2025-11-01'),       -- ID: 10
       ('Mia', 'Parnevik', 'Student', '2025-12-01'),         -- ID: 11
       ('Noel', 'Floren', 'Standard', '2025-11-12'),         -- ID: 12
       ('Markoolio', 'Lehtosalo', 'Premium', '2025-10-05'),  -- ID: 13
       ('Joel', 'Kinnaman', 'Student', '2025-11-25'),        -- ID: 14
       ('Zlatan', 'Ibrahimović', 'Premium', '2025-09-15'),   -- ID: 15
       ('Mikael', 'Persbrandt', 'Premium', '2025-10-20'),    -- ID: 16
       ('Danny', 'Saucedo', 'Student', '2025-12-05'),        -- ID: 17
       ('Viktoria', 'Bernadotte', 'Standard', '2025-08-01'), -- ID: 18
       ('Björn', 'Skifs', 'Student', '2025-11-11'),          -- ID: 19
       ('Avicii', 'Bergling', 'Premium', '2025-10-10');      -- ID: 20

-- ==========================================
-- 2. VEHICLES (IDs will be 1 - 16)
-- ==========================================
INSERT INTO vehicles (make, model, year, type, capacity, cost, is_rented)
VALUES ('Ford', 'E-Series RV', '2019', 'Motorhome', '8', 1223.00, 0),           -- ID: 1
       ('Hobby', 'De Luxe 545', '2020', 'Caravan', '4', 752.00, 0),             -- ID: 2
       ('Mercedes-Benz', 'Sprinter Van', '2022', 'Campervan', '2', 1500.00, 1), -- ID: 3
       ('Kabe', 'Estate 630', '2019', 'Caravan', '3', 600.00, 0),               -- ID: 4
       ('Fiat', 'Ducato 4x4', '2021', 'Campervan', '4', 1350.00, 0),            -- ID: 5
       ('Citroën', 'Jumper RV', '2017', 'Motorhome', '4', 1050.00, 0),          -- ID: 6
       ('Polar', 'Edition 590', '2019', 'Caravan', '5', 820.00, 0),             -- ID: 7
       ('Polar', 'Mini Caravan', '2020', 'Caravan', '2', 550.00, 0),            -- ID: 8
       ('Fendt', 'Opal 560', '2022', 'Caravan', '5', 950.00, 1),                -- ID: 9
       ('Dethleffs', 'Globetrotter', '2024', 'Motorhome', '6', 2100.00, 0),     -- ID: 10
       ('Eriba', 'Touring 310', '2021', 'Caravan', '3', 700.00, 0),             -- ID: 11
       ('Peugeot', 'Boxer Vanlife', '2020', 'Campervan', '2', 1150.00, 0),      -- ID: 12
       ('Chausson', 'Welcome 747', '2018', 'Motorhome', '7', 1650.00, 0),       -- ID: 13
       ('Bürstner', 'Premio Life 420', '2023', 'Caravan', '4', 650.00, 0),      -- ID: 14
       ('Renault', 'Master Camper', '2019', 'Campervan', '3', 980.00, 0),       -- ID: 15
       ('Niesmann+Bischoff', 'Arto 77E', '2024', 'Motorhome', '4', 2500.00, 0); -- ID: 16

-- ==========================================
-- 3. GEAR (IDs will be 1 - 9)
-- (Removed the Tent from here)
-- ==========================================
INSERT INTO gear (model, type, capacity, cost, is_rented)
VALUES ('Outdoor Grill Set', 'Other Gear', 'N/A', 110.00, 0),      -- ID: 1
       ('Junior Hiker 15L', 'Backpack', '15L', 33.00, 0),          -- ID: 2 (Prev 3)
       ('Expedition 110L', 'Backpack', '110L', 150.00, 0),         -- ID: 3 (Prev 4)
       ('Portable Gas Stove', 'Other Gear', 'N/A', 25.00, 1),      -- ID: 4 (Prev 5)
       ('Double Hammock Pro', 'Other Gear', '2 People', 45.00, 0), -- ID: 5 (Prev 6)
       ('Solar Lantern', 'Other Gear', 'N/A', 15.00, 0),           -- ID: 6 (Prev 7)
       ('Water Filter', 'Other Gear', '1000L', 35.00, 0),          -- ID: 7 (Prev 8)
       ('Sleeping Bag -20C', 'Other Gear', '1 Person', 60.00, 0),  -- ID: 8 (Prev 9)
       ('Camelback 3000', 'Other Gear', '3L', 25.00, 0);           -- ID: 9 (Prev 10)

-- ==========================================
-- 4. TENTS (IDs will be 1)
-- (Moved the Tent here)
-- ==========================================
INSERT INTO tents (model, capacity, cost, is_rented)
VALUES ('Arctic Shield 4S', '4 People', 450.00, 1); -- ID: 1 (Prev Gear ID 2)


-- ==========================================
-- 5. RENTALS (IDs 1-6)
-- FKs updated to match new IDs from above
-- ==========================================

-- Member: Mikael (16) | Item: Ford RV (Vehicle ID 1)
INSERT INTO rentals (member_id, item_id, item_type, rental_date, return_date, rental_days, total_cost)
VALUES (16, 1, 'VEHICLE', '2026-01-05', '2026-01-12', 7, 8561.00);

-- Member: Peter (6) | Item: Arctic Shield Tent (NOW Tent ID 1)
INSERT INTO rentals (member_id, item_id, item_type, rental_date, return_date, rental_days, total_cost)
VALUES (6, 1, 'TENT', '2026-01-06', '2026-01-09', 3, 1350.00);

-- Member: Mia (11) | Item: Sprinter (Vehicle ID 3)
INSERT INTO rentals (member_id, item_id, item_type, rental_date, return_date, rental_days, total_cost)
VALUES (11, 3, 'VEHICLE', '2026-01-10', '2026-01-24', 14, 21000.00);

-- Member: Stellan (3) | Item: Gas Stove (NOW Gear ID 4, was ID 5)
INSERT INTO rentals (member_id, item_id, item_type, rental_date, return_date, rental_days, total_cost)
VALUES (3, 4, 'GEAR', '2026-01-12', '2026-01-14', 2, 50.00);

-- Member: Zlatan (15) | Item: Niesmann RV (Vehicle ID 16)
INSERT INTO rentals (member_id, item_id, item_type, rental_date, return_date, rental_days, total_cost)
VALUES (15, 16, 'VEHICLE', '2026-01-15', '2026-02-14', 30, 75000.00);

-- Member: Malin (7) | Item: Fendt Caravan (Vehicle ID 9)
INSERT INTO rentals (member_id, item_id, item_type, rental_date, return_date, rental_days, total_cost)
VALUES (7, 9, 'VEHICLE', '2026-01-20', '2026-01-27', 7, 6650.00);


-- ==========================================
-- 6. MEMBER HISTORY
-- FKs updated to match new Member IDs
-- ==========================================
INSERT INTO member_history (member_id, event_description)
VALUES (16, 'Rented Ford E-Series RV (Motorhome) on 2026-01-05'),           -- Mikael
       (6, 'Rented Arctic Shield 4S (Tent) on 2026-01-06'),                 -- Peter
       (11, 'Rented Mercedes-Benz Sprinter Van (Campervan) on 2026-01-10'), -- Mia
       (3, 'Rented Portable Gas Stove (Other Gear) on 2026-01-12'),         -- Stellan
       (15, 'Rented Niesmann+Bischoff Arto 77E (Motorhome) on 2026-01-15'), -- Zlatan
       (7, 'Rented Fendt Opal 560 (Caravan) on 2026-01-20');                -- Malin


-- ==========================================
-- 7. PROFITS
-- ==========================================
INSERT INTO profits (date, amount)
VALUES ('2026-01-05', 1223.00),
       ('2026-01-06', 1673.00),
       ('2026-01-07', 1673.00),
       ('2026-01-08', 1673.00),
       ('2026-01-09', 1223.00),
       ('2026-01-10', 2723.00),
       ('2026-01-11', 2723.00),
       ('2026-01-12', 1525.00),
       ('2026-01-13', 1525.00),
       ('2026-01-14', 1500.00),
       ('2026-01-15', 4000.00),
       ('2026-01-16', 4000.00),
       ('2026-01-17', 4000.00),
       ('2026-01-18', 4000.00),
       ('2026-01-19', 4000.00),
       ('2026-01-20', 4950.00);

-- Final Check
SELECT * FROM members;