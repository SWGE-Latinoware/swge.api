ALTER TABLE users
DROP COLUMN IF EXISTS gender,
DROP COLUMN IF EXISTS job_title;

ALTER TABLE tutored_user
DROP COLUMN IF EXISTS gender;

ALTER TABLE users
ADD COLUMN IF NOT EXISTS exclusion_date timestamp;

ALTER TABLE tutored_user
ADD COLUMN IF NOT EXISTS exclusion_date timestamp;
