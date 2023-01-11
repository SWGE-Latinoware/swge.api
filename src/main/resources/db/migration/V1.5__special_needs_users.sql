ALTER TABLE users
  ADD COLUMN IF NOT EXISTS other_needs varchar (255),
  ADD COLUMN IF NOT EXISTS needs_types varchar;

ALTER TABLE tutored_user
  ADD COLUMN IF NOT EXISTS other_needs varchar (255),
  ADD COLUMN IF NOT EXISTS needs_types varchar;
