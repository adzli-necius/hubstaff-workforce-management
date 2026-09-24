-- System roles used by authorization. INSERT IGNORE preserves any existing data.
INSERT IGNORE INTO roles (name) VALUES
    ('ADMIN'),
    ('MANAGER'),
    ('EMPLOYEE');

-- Default leave types for a new workforce-management installation.
-- Organisations can add their own leave types without this migration overwriting them.
INSERT IGNORE INTO leave_types (code, name, max_days, is_active, created_at) VALUES
    ('ANNUAL', 'Annual Leave', 14.0, TRUE, CURRENT_TIMESTAMP),
    ('MEDICAL', 'Medical Leave', 14.0, TRUE, CURRENT_TIMESTAMP),
    ('EMERGENCY', 'Emergency Leave', 3.0, TRUE, CURRENT_TIMESTAMP),
    ('UNPAID', 'Unpaid Leave', 0.0, TRUE, CURRENT_TIMESTAMP);
