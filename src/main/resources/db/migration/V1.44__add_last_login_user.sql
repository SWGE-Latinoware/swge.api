ALTER TABLE users
ADD COLUMN IF NOT EXISTS last_login timestamp;
