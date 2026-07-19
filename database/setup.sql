-- ============================================================
--  Smart Bus Hygiene Monitoring System - MySQL Setup (FP)
--  NEW database name: sbhms_db  (avoids conflict with old project)
--  Run this ONCE in MySQL Workbench before starting the app
-- ============================================================

CREATE DATABASE IF NOT EXISTS sbhms_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE sbhms_db;

-- 1. sbhms_admin
CREATE TABLE IF NOT EXISTS sbhms_admin (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    name       VARCHAR(100) NOT NULL
);
INSERT INTO sbhms_admin (email, password, name) VALUES
('admin@busfeedback.in', 'admin123', 'Admin User')
ON DUPLICATE KEY UPDATE id=id;

-- 2. sbhms_bus
CREATE TABLE IF NOT EXISTS sbhms_bus (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    bus_code    VARCHAR(20)  NOT NULL UNIQUE,
    route_name  VARCHAR(150) NOT NULL
);
INSERT INTO sbhms_bus (bus_code, route_name) VALUES
('BUS001', 'Route 1 - City Center to Airport'),
('BUS002', 'Route 2 - Central to Tech Park'),
('BUS003', 'Route 3 - North to South Station'),
('BUS004', 'Route 4 - Market to University'),
('BUS005', 'Route 5 - East to West Terminal')
ON DUPLICATE KEY UPDATE id=id;

-- 3. sbhms_feedback  (complaint_id added, no toilet column)
CREATE TABLE IF NOT EXISTS sbhms_feedback (
    id              BIGINT        AUTO_INCREMENT PRIMARY KEY,
    complaint_id    VARCHAR(25)   NOT NULL UNIQUE,
    bus_id          VARCHAR(20)   NOT NULL,
    route           VARCHAR(150)  NOT NULL,
    rating          TINYINT       DEFAULT 0,
    bad_smell       TINYINT(1)    DEFAULT 0,
    muddy_floor     TINYINT(1)    DEFAULT 0,
    dirty_seats     TINYINT(1)    DEFAULT 0,
    vomit           TINYINT(1)    DEFAULT 0,
    cleaning_areas  VARCHAR(200)  DEFAULT '',
    complaint       TEXT,
    status          VARCHAR(20)   DEFAULT 'Pending',
    submitted_at    DATETIME      DEFAULT CURRENT_TIMESTAMP
);

-- 4. sbhms_alerts  (one alert per feedback — enforced by feedback_id UNIQUE)
CREATE TABLE IF NOT EXISTS sbhms_alerts (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    complaint_id VARCHAR(25)  NOT NULL,
    bus_id       VARCHAR(20)  NOT NULL,
    alert_type   VARCHAR(50)  NOT NULL,
    message      TEXT,
    status       VARCHAR(20)  DEFAULT 'Pending',
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    feedback_id  BIGINT       UNIQUE,
    FOREIGN KEY (feedback_id) REFERENCES sbhms_feedback(id) ON DELETE SET NULL
);

-- 5. sbhms_hygiene_status
CREATE TABLE IF NOT EXISTS sbhms_hygiene_status (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    bus_id          VARCHAR(20)  NOT NULL UNIQUE,
    is_clean        TINYINT(1)   DEFAULT 1,
    complaint_count INT          DEFAULT 0,
    last_updated    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
INSERT INTO sbhms_hygiene_status (bus_id, is_clean, complaint_count) VALUES
('BUS001', 1, 0), ('BUS002', 1, 0), ('BUS003', 1, 0),
('BUS004', 1, 0), ('BUS005', 1, 0)
ON DUPLICATE KEY UPDATE id=id;
