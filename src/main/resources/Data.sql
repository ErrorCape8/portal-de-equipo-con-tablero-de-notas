INSERT IGNORE INTO users (name, email, password, role, active)
VALUES
    ('Admin demo', 'admin@test.com', '$2a$14$mjvGkcrnPz3lN0/nGZLbEOfpK0cvlCsXCAQIjKbm6Vry6GlvTC2Yy', 'ADMIN', true),
    ('User demo', 'user@test.com', '$2a$14$mjvGkcrnPz3lN0/nGZLbEOfpK0cvlCsXCAQIjKbm6Vry6GlvTC2Yy', 'USER', true);