alter table activity
  add column if not exists calendar_color varchar(255);

create table if not exists schedule
(
    id              bigserial
        primary key,
    all_day         boolean   not null,
    end_date_time   timestamp not null,
    start_date_time timestamp not null,
    activity_id     bigint
        constraint fkebh4a3xaec5w8se7hf2hv7nx2
            references activity
);
