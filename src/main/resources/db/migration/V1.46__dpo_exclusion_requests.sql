ALTER TABLE users
DROP COLUMN IF EXISTS exclusion_date;

ALTER TABLE tutored_user
DROP COLUMN IF EXISTS exclusion_date;
