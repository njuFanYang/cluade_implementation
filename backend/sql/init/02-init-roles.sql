-- Initialize Default Roles
-- Created: 2026-02-20
-- Description: Inserts default roles for RBAC system

USE musicshare;

-- Insert default roles if they don't exist
INSERT IGNORE INTO roles (name, display_name, description, created_at)
VALUES
    ('ROLE_USER', 'User', 'Regular user with basic permissions', NOW()),
    ('ROLE_MUSICIAN', 'Musician', 'Can upload and manage music content', NOW()),
    ('ROLE_ADMIN', 'Administrator', 'Full administrative access to the system', NOW());

-- Show inserted roles
SELECT * FROM roles;
