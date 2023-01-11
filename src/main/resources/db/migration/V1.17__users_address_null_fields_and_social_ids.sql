ALTER TABLE users
  ALTER COLUMN country drop not null,
  ALTER COLUMN zip_code drop not null,
  ALTER COLUMN state drop not null,
  ALTER COLUMN city drop not null,
  ALTER COLUMN address_line1 drop not null,
  add column if not exists github_id varchar(255),
  add column if not exists google_id varchar(255);
