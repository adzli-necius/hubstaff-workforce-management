CREATE TABLE initial_admin_bootstrap (
    id BIGINT NOT NULL,
    completed_at DATETIME NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Existing installations that already have an admin must never auto-create a
-- replacement account if bootstrap variables are later enabled.
INSERT INTO initial_admin_bootstrap (id, completed_at)
SELECT 1, CURRENT_TIMESTAMP
WHERE EXISTS (
    SELECT 1
    FROM user_accounts account
    JOIN user_account_roles account_role ON account_role.user_account_id = account.id
    JOIN roles role ON role.id = account_role.role_id
    WHERE UPPER(role.name) = 'ADMIN'
)
ON DUPLICATE KEY UPDATE completed_at = completed_at;
