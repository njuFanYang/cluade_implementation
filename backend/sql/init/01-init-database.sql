-- MusicShare Database Initialization Script
-- Created: 2026-02-20
-- Description: Creates the musicshare database and sets proper charset

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS musicshare
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Use the database
USE musicshare;

-- Show success message
SELECT 'Database musicshare created successfully!' as message;
