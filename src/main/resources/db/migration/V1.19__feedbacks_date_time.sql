ALTER TABLE feedback
  add column IF NOT EXISTS creation_date_time timestamp not null,
  add column IF NOT EXISTS modification_date_time timestamp not null;
