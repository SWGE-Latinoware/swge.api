ALTER TABLE activity
drop column if exists calendar_color;

ALTER TABLE track
add column if not exists calendar_color varchar;
