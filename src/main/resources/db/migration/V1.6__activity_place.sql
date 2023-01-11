ALTER TABLE activity
  ADD COLUMN if not exists place     varchar(255),
  ADD COLUMN if not exists place_url varchar(255);

