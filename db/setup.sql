-- Run once with a MySQL account allowed to create databases.
CREATE DATABASE IF NOT EXISTS careerhub
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

-- Hibernate creates/updates the normalized tables on first application start.
-- For production, use DDL_AUTO=validate and apply reviewed schema migrations.
