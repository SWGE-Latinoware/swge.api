ALTER TABLE registration
ADD COLUMN if not exists identifier varchar,
ADD COLUMN if not exists payment_type integer default 0;
