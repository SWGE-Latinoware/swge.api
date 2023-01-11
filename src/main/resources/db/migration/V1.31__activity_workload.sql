ALTER TABLE activity
  drop if exists workload,
  ADD COLUMN IF NOT EXISTS workload varchar;

UPDATE activity
SET workload = '00:00'
FROM activity ir
WHERE ir.workload is null;

ALTER TABLE activity
  ALTER COLUMN workload SET NOT NULL;
