-- Overtime sessions are separate from the one-record-per-day attendances table.
-- Run this migration once against the hubstaff MariaDB database.

CREATE TABLE IF NOT EXISTS overtime_records (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    attendance_id BIGINT NOT NULL,
    overtime_start DATETIME NOT NULL,
    overtime_end DATETIME NULL,

    start_latitude DECIMAL(10, 7) NULL,
    start_longitude DECIMAL(10, 7) NULL,
    start_accuracy DECIMAL(10, 2) NULL,
    end_latitude DECIMAL(10, 7) NULL,
    end_longitude DECIMAL(10, 7) NULL,
    end_accuracy DECIMAL(10, 2) NULL,

    requested_minutes INT NULL,
    approved_minutes INT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reason VARCHAR(500) NULL,
    manager_note VARCHAR(500) NULL,
    approved_by BIGINT NULL,
    approved_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_overtime_employee
        FOREIGN KEY (employee_id) REFERENCES employees (id),
    CONSTRAINT fk_overtime_attendance
        FOREIGN KEY (attendance_id) REFERENCES attendances (id),
    CONSTRAINT fk_overtime_approved_by
        FOREIGN KEY (approved_by) REFERENCES user_accounts (id),
    CONSTRAINT chk_overtime_status
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    CONSTRAINT chk_overtime_minutes
        CHECK (requested_minutes IS NULL OR requested_minutes >= 0),
    CONSTRAINT chk_approved_minutes
        CHECK (approved_minutes IS NULL OR approved_minutes >= 0),
    INDEX idx_overtime_employee (employee_id),
    INDEX idx_overtime_attendance (attendance_id),
    INDEX idx_overtime_status (status),
    INDEX idx_overtime_start (overtime_start)
) ENGINE=InnoDB;
