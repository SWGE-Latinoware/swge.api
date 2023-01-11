ALTER TABLE space
  ADD COLUMN if not exists number varchar;

UPDATE space
SET number = 0
FROM space sp
WHERE sp.number is null;

ALTER TABLE space
  ALTER COLUMN number SET NOT NULL;
