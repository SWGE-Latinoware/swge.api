ALTER TABLE individual_registration_schedule
    alter column individual_registration_id set not null,
    alter column schedule_id set not null;
