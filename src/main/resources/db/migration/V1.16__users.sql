ALTER TABLE users
  ALTER COLUMN zip_code set not null,
  ALTER COLUMN state set not null,
  ALTER COLUMN city set not null,
  ALTER COLUMN address_line1 set not null;
