UPDATE registration
SET payment_type = 0
  FROM registration rg
WHERE rg.payment_type is null;

ALTER TABLE registration
  ALTER COLUMN payment_type SET NOT NULL;
