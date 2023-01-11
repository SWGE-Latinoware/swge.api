ALTER TABLE users
  ADD COLUMN IF NOT EXISTS social_communication boolean not null default false;
