ALTER TABLE feedback
  add column if not exists api_version varchar,
  add column if not exists web_version varchar;
