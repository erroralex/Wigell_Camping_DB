-- ==========================================
-- 1. MEMBERS
-- ==========================================
INSERT INTO members (first_name, last_name, level, entered_date)
VALUES ('Felix', 'Kjellberg', 'PREMIUM', '2025-08-01'),
       ('Tomas', 'Wigell', 'PREMIUM', '2025-08-05'),
       ('Stellan', 'Skarsgård', 'STUDENT', '2025-08-10'),
       ('Kevin', 'Costner', 'PREMIUM', '2025-08-12'),
       ('Sarah', 'Sjöström', 'PREMIUM', '2025-08-15'),
       ('Peter', 'Forsberg', 'STANDARD', '2025-08-20'),
       ('Malin', 'Åkerman', 'STANDARD', '2025-09-01'),
       ('Kurt', 'Olsson', 'STANDARD', '2025-09-05'),
       ('Carola', 'Häggkvist', 'STANDARD', '2025-09-10'),
       ('Robyn', 'Carlsson', 'PREMIUM', '2025-09-15'),
       ('Mia', 'Parnevik', 'STUDENT', '2025-09-20'),
       ('Noel', 'Floren', 'STANDARD', '2025-10-01'),
       ('Markoolio', 'Lehtosalo', 'PREMIUM', '2025-10-05'),
       ('Joel', 'Kinnaman', 'STUDENT', '2025-10-10'),
       ('Zlatan', 'Ibrahimović', 'PREMIUM', '2025-10-15'),
       ('Mikael', 'Persbrandt', 'PREMIUM', '2025-10-20'),
       ('Danny', 'Saucedo', 'STUDENT', '2025-11-01'),
       ('Viktoria', 'Bernadotte', 'STANDARD', '2025-11-05'),
       ('Björn', 'Skifs', 'STUDENT', '2025-11-10'),
       ('Avicii', 'Bergling', 'PREMIUM', '2025-11-15');

-- ==========================================
-- 2. VEHICLES
-- ==========================================
INSERT INTO vehicles (make, model, year, type, capacity, cost, is_rented)
VALUES ('Hymer', 'Grand Canyon S', '2024', 'Campervan', '4', 1850.00, false),
       ('Volkswagen', 'California Ocean', '2023', 'Campervan', '4', 1600.00, false),
       ('Concorde', 'Liner 1090', '2022', 'Motorhome', '6', 4500.00, false),
       ('Kabe', 'Imperial 1000', '2023', 'Caravan', '8', 1200.00, false),
       ('Mercedes', 'Marco Polo', '2024', 'Campervan', '2', 1750.00, false),
       ('Adria', 'Supersonic 780', '2023', 'Motorhome', '5', 2200.00, false),
       ('Airstream', 'Flying Cloud', '2020', 'Caravan', '4', 1500.00, false),
       ('Winnebago', 'Ekko', '2022', 'Motorhome', '4', 2100.00, false),
       ('Tesla', 'Cybertruck + Basecamp', '2024', 'Campervan', '2', 3000.00, false),
       ('Scania', 'Expedition Truck', '2021', 'Motorhome', '6', 3500.00, true),
       ('Polar', 'Blackline 730', '2022', 'Caravan', '5', 950.00, false),
       ('Hobby', 'Beachy 450', '2023', 'Caravan', '3', 600.00, false),
       ('Ford', 'Nugget Plus', '2023', 'Campervan', '4', 1400.00, false),
       ('Morelo', 'Palace 88', '2020', 'Motorhome', '4', 3800.00, false),
       ('Volvo', 'Valp Overlander', '1980', 'Campervan', '2', 800.00, false);

-- ==========================================
-- 3. GEAR
-- ==========================================
INSERT INTO gear (model, type, capacity, cost, is_rented)
VALUES ('Weber Traveler', 'Gear', 'N/A', 150.00, false),
       ('Osprey Aether 65', 'Gear', '65L', 80.00, false),
       ('Jackery Explorer 1000', 'Gear', '1000W', 250.00, false),
       ('Garmin GPSMAP 66i', 'Gear', 'N/A', 100.00, false),
       ('Scott Strike eRIDE', 'Gear', 'N/A', 650.00, false),
       ('Scott Strike eRIDE', 'Gear', 'N/A', 650.00, false),
       ('Ooni Karu 12', 'Gear', 'N/A', 200.00, false),
       ('Starlink Roam', 'Gear', 'Unlimited', 300.00, false),
       ('Yeti Tundra 45', 'Gear', '45L', 120.00, false),
       ('Thule Hull-a-Port', 'Gear', '2 Kayaks', 150.00, false),
       ('Advanced Elements', 'Gear', '2 People', 350.00, true),
       ('GoPro Hero 12', 'Gear', 'N/A', 150.00, false);

-- ==========================================
-- 4. TENTS
-- ==========================================
INSERT INTO tents (model, capacity, cost, is_rented)
VALUES ('Thule Tepui Explorer', '3 People (Roof)', 600.00, false),
       ('Nordisk Asgard 12.6', '6 People (Glamping)', 900.00, true),
       ('Hilleberg Keron 4 GT', '4 People', 1100.00, false),
       ('Big Agnes Copper Spur', '2 People', 450.00, false),
       ('Coleman Instant Cabin', '8 People', 500.00, false),
       ('Heimplanet The Cave', '3 People', 750.00, false),
       ('Fjällräven Abisko View', '2 People', 550.00, false),
       ('Dometic HUB 2 Shelter', 'N/A', 400.00, false);

-- ==========================================
-- 5. RENTALS
-- ==========================================
INSERT INTO rentals (member_id, item_id, item_type, rental_date, return_date, rental_days, total_cost)
VALUES (1, 14, 'VEHICLE', '2025-10-01', '2025-10-08', 7, 26600.00),
       (2, 4, 'TENT', '2025-10-05', '2025-10-07', 2, 900.00),
       (3, 2, 'GEAR', '2025-10-10', '2025-10-15', 5, 400.00),
       (4, 9, 'GEAR', '2025-10-12', '2025-10-14', 2, 240.00),
       (5, 5, 'VEHICLE', '2025-11-01', '2025-11-05', 4, 7000.00),
       (5, 11, 'GEAR', '2025-11-01', '2025-11-05', 4, 1400.00),
       (6, 15, 'VEHICLE', '2025-11-10', '2025-11-12', 2, 1600.00),
       (7, 2, 'TENT', '2025-11-15', '2025-11-18', 3, 2700.00),
       (8, 12, 'VEHICLE', '2025-11-20', '2025-11-22', 2, 1200.00),
       (13, 10, 'VEHICLE', '2026-01-05', '2026-01-12', 7, 24500.00),
       (17, 6, 'TENT', '2026-01-10', '2026-01-14', 4, 3000.00),
       (14, 12, 'GEAR', '2026-01-12', '2026-01-16', 4, 600.00),
       (12, 7, 'GEAR', '2026-01-15', '2026-01-18', 3, 600.00),
       (11, 4, 'VEHICLE', '2026-01-10', '2026-01-20', 10, 12000.00),
       (10, 8, 'GEAR', '2026-01-15', '2026-01-22', 7, 2100.00),
       (9, 6, 'VEHICLE', '2026-01-20', '2026-01-25', 5, 11000.00),
       (19, 1, 'GEAR', '2026-01-24', '2026-01-25', 1, 150.00),
       (15, 3, 'VEHICLE', '2026-01-20', '2026-01-29', 9, 40500.00),
       (15, 5, 'GEAR', '2026-01-20', '2026-01-29', 9, 5850.00),
       (15, 6, 'GEAR', '2026-01-20', '2026-01-29', 9, 5850.00),
       (15, 1, 'TENT', '2026-01-20', '2026-01-29', 9, 5400.00),
       (20, 3, 'GEAR', '2026-01-23', '2026-01-28', 5, 1250.00),
       (16, 1, 'VEHICLE', '2026-01-24', '2026-01-27', 3, 5550.00),
       (18, 7, 'VEHICLE', '2026-01-25', '2026-01-26', 1, 1500.00),
       (1, 14, 'VEHICLE', '2026-01-28', '2026-01-30', 2, 7600.00),
       (2, 4, 'TENT', '2026-01-29', '2026-01-31', 2, 900.00),
       (3, 1, 'GEAR', '2026-01-30', '2026-02-01', 2, 300.00),
       (4, 5, 'VEHICLE', '2026-01-31', '2026-02-02', 2, 3500.00),
       (5, 8, 'TENT', '2026-02-01', '2026-02-03', 2, 800.00),
       (6, 9, 'GEAR', '2026-02-02', '2026-02-04', 2, 240.00),
       (7, 2, 'VEHICLE', '2026-02-02', '2026-02-05', 3, 4800.00),
       (8, 10, 'VEHICLE', '2026-02-04', NULL, NULL, NULL),
       (9, 2, 'TENT', '2026-02-05', NULL, NULL, NULL),
       (10, 11, 'GEAR', '2026-02-05', NULL, NULL, NULL);

-- ==========================================
-- 6. MEMBER HISTORY
-- ==========================================
INSERT INTO member_history (member_id, event_description)
VALUES (1, 'Rented Morelo Palace 88 (Motorhome) on 2025-10-01'),
       (2, 'Rented Big Agnes Copper Spur (Tent) on 2025-10-05'),
       (3, 'Rented Osprey Aether 65 (Gear) on 2025-10-10'),
       (4, 'Rented Yeti Tundra 45 (Gear) on 2025-10-12'),
       (5, 'Rented Mercedes Marco Polo (Campervan) on 2025-11-01'),
       (5, 'Rented Advanced Elements (Gear) on 2025-11-01'),
       (6, 'Rented Volvo Valp Overlander (Campervan) on 2025-11-10'),
       (7, 'Rented Nordisk Asgard 12.6 (Tent) on 2025-11-15'),
       (8, 'Rented Hobby Beachy 450 (Caravan) on 2025-11-20'),
       (13, 'Rented Scania Expedition Truck (Motorhome) on 2026-01-05'),
       (17, 'Rented Heimplanet The Cave (Tent) on 2026-01-10'),
       (11, 'Rented Kabe Imperial 1000 (Caravan) on 2026-01-10'),
       (14, 'Rented GoPro Hero 12 (Gear) on 2026-01-12'),
       (12, 'Rented Ooni Karu 12 (Gear) on 2026-01-15'),
       (10, 'Rented Starlink Roam (Gear) on 2026-01-15'),
       (15, 'Rented Concorde Liner 1090 (Motorhome) on 2026-01-20'),
       (15, 'Rented Scott Strike eRIDE (Gear) on 2026-01-20'),
       (15, 'Rented Scott Strike eRIDE (Gear) on 2026-01-20'),
       (15, 'Rented Thule Tepui Explorer (Tent) on 2026-01-20'),
       (9, 'Rented Adria Supersonic 780 (Motorhome) on 2026-01-20'),
       (20, 'Rented Jackery Explorer 1000 (Gear) on 2026-01-23'),
       (16, 'Rented Hymer Grand Canyon S (Campervan) on 2026-01-24'),
       (19, 'Rented Weber Traveler (Gear) on 2026-01-24'),
       (18, 'Rented Airstream Flying Cloud (Caravan) on 2026-01-25'),
       (1, 'Rented Morelo Palace 88 (Motorhome) on 2026-01-28'),
       (2, 'Rented Big Agnes Copper Spur (Tent) on 2026-01-29'),
       (3, 'Rented Weber Traveler (Gear) on 2026-01-30'),
       (4, 'Rented Mercedes Marco Polo (Campervan) on 2026-01-31'),
       (5, 'Rented Dometic HUB 2 Shelter (Tent) on 2026-02-01'),
       (6, 'Rented Yeti Tundra 45 (Gear) on 2026-02-02'),
       (7, 'Rented Volkswagen California Ocean (Campervan) on 2026-02-02'),
       (8, 'Rented Scania Expedition Truck (Motorhome) on 2026-02-04'),
       (9, 'Rented Nordisk Asgard 12.6 (Tent) on 2026-02-05'),
       (10, 'Rented Advanced Elements (Gear) on 2026-02-05'),

       -- Returns
       (1, 'Returned Morelo Palace 88 (Motorhome) on 2025-10-08'),
       (2, 'Returned Big Agnes Copper Spur (Tent) on 2025-10-07'),
       (3, 'Returned Osprey Aether 65 (Gear) on 2025-10-15'),
       (4, 'Returned Yeti Tundra 45 (Gear) on 2025-10-14'),
       (5, 'Returned Mercedes Marco Polo (Campervan) on 2025-11-05'),
       (5, 'Returned Advanced Elements (Gear) on 2025-11-05'),
       (6, 'Returned Volvo Valp Overlander (Campervan) on 2025-11-12'),
       (7, 'Returned Nordisk Asgard 12.6 (Tent) on 2025-11-18'),
       (8, 'Returned Hobby Beachy 450 (Caravan) on 2025-11-22'),
       (13, 'Returned Scania Expedition Truck (Motorhome) on 2026-01-12'),
       (17, 'Returned Heimplanet The Cave (Tent) on 2026-01-14'),
       (14, 'Returned GoPro Hero 12 (Gear) on 2026-01-16'),
       (12, 'Returned Ooni Karu 12 (Gear) on 2026-01-18'),
       (11, 'Returned Kabe Imperial 1000 (Caravan) on 2026-01-20'),
       (10, 'Returned Starlink Roam (Gear) on 2026-01-22'),
       (9, 'Returned Adria Supersonic 780 (Motorhome) on 2026-01-25'),
       (19, 'Returned Weber Traveler (Gear) on 2026-01-25'),
       (18, 'Returned Airstream Flying Cloud (Caravan) on 2026-01-26'),
       (16, 'Returned Hymer Grand Canyon S (Campervan) on 2026-01-27'),
       (20, 'Returned Jackery Explorer 1000 (Gear) on 2026-01-28'),
       (15, 'Returned Concorde Liner 1090 (Motorhome) on 2026-01-29'),
       (15, 'Returned Scott Strike eRIDE (Gear) on 2026-01-29'),
       (15, 'Returned Scott Strike eRIDE (Gear) on 2026-01-29'),
       (15, 'Returned Thule Tepui Explorer (Tent) on 2026-01-29'),
       (1, 'Returned Morelo Palace 88 (Motorhome) on 2026-01-30'),
       (2, 'Returned Big Agnes Copper Spur (Tent) on 2026-01-31'),
       (3, 'Returned Weber Traveler (Gear) on 2026-02-01'),
       (4, 'Returned Mercedes Marco Polo (Campervan) on 2026-02-02'),
       (5, 'Returned Dometic HUB 2 Shelter (Tent) on 2026-02-03'),
       (6, 'Returned Yeti Tundra 45 (Gear) on 2026-02-04'),
       (7, 'Returned Volkswagen California Ocean (Campervan) on 2026-02-05');

-- ==========================================
-- 7. PROFITS
-- ==========================================
INSERT INTO profits (date, amount)
VALUES ('2025-10-08', 26600.00),
       ('2025-10-07', 900.00),
       ('2025-10-14', 240.00),
       ('2025-10-15', 400.00),
       ('2025-11-05', 8400.00),
       ('2025-11-12', 1600.00),
       ('2025-11-18', 2700.00),
       ('2025-11-22', 1200.00),
       ('2026-01-12', 24500.00),
       ('2026-01-14', 3000.00),
       ('2026-01-16', 600.00),
       ('2026-01-18', 600.00),
       ('2026-01-20', 12000.00),
       ('2026-01-22', 2100.00),
       ('2026-01-25', 11150.00),
       ('2026-01-26', 1500.00),
       ('2026-01-27', 5550.00),
       ('2026-01-28', 1250.00),
       ('2026-01-29', 57600.00),
       ('2026-01-30', 7600.00),
       ('2026-01-31', 900.00),
       ('2026-02-01', 300.00),
       ('2026-02-02', 3500.00),
       ('2026-02-03', 800.00),
       ('2026-02-04', 240.00),
       ('2026-02-05', 4800.00);