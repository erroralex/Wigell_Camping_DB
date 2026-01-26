-- ==========================================
-- DATABASE & CLEANUP
-- ==========================================
CREATE DATABASE IF NOT EXISTS wigell_camping_members_club;
USE wigell_camping_members_club;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS member_history;
DROP TABLE IF EXISTS rentals;
DROP TABLE IF EXISTS profits;
DROP TABLE IF EXISTS tents;
DROP TABLE IF EXISTS gear;
DROP TABLE IF EXISTS vehicles;
DROP TABLE IF EXISTS members;
SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================
-- 1. MEMBERS
-- ==========================================
CREATE TABLE members
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    level        VARCHAR(50)  NOT NULL,
    entered_date DATE         NOT NULL
);

-- ==========================================
-- 2. MEMBER HISTORY
-- ==========================================
CREATE TABLE member_history
(
    id                INT PRIMARY KEY AUTO_INCREMENT,
    member_id         INT NOT NULL,
    event_description VARCHAR(255),
    FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE CASCADE
);

-- ==========================================
-- 3. GEAR
-- ==========================================
CREATE TABLE gear
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    model     VARCHAR(100) NOT NULL,
    type      VARCHAR(50)  NOT NULL,
    capacity  VARCHAR(50),
    cost      DECIMAL(10, 2) NOT NULL,
    is_rented BOOLEAN DEFAULT 0
);

-- ==========================================
-- 4. TENTS
-- ==========================================
CREATE TABLE tents
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    model     VARCHAR(100) NOT NULL,
    capacity  VARCHAR(50),
    cost      DECIMAL(10, 2) NOT NULL,
    is_rented BOOLEAN DEFAULT 0
);

-- ==========================================
-- 5. VEHICLES
-- ==========================================
CREATE TABLE vehicles
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    make      VARCHAR(50)  NOT NULL,
    model     VARCHAR(100) NOT NULL,
    year      VARCHAR(10)  NOT NULL,
    type      VARCHAR(50)  NOT NULL,
    capacity  VARCHAR(50),
    cost      DECIMAL(10, 2) NOT NULL,
    is_rented BOOLEAN DEFAULT 0
);

-- ==========================================
-- 6. RENTALS
-- ==========================================
CREATE TABLE rentals
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    member_id   INT,
    item_id     INT NOT NULL,
    item_type   VARCHAR(50) NOT NULL,
    rental_date DATE NOT NULL,
    return_date DATE,
    rental_days INT,
    total_cost  DECIMAL(10, 2),
    FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE SET NULL
);

-- ==========================================
-- 7. PROFITS
-- ==========================================
CREATE TABLE profits
(
    id     INT PRIMARY KEY AUTO_INCREMENT,
    date   DATE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL
);