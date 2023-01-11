create table if not exists individual_registration_schedule
(
  id                         bigserial
    primary key,
  individual_registration_id bigint
    constraint fk9e2ru32hhaegbenrrxm191mas
      references individual_registration,
  schedule_id                bigint
    constraint fkbhtvpwnde86r44pn42iakjhq3
      references schedule
);

ALTER TABLE individual_registration
  add column if not exists custom_schedule boolean;

UPDATE individual_registration
SET custom_schedule = false
FROM individual_registration ir
WHERE ir.custom_schedule is null;

ALTER TABLE individual_registration
  ALTER COLUMN custom_schedule SET NOT NULL;
