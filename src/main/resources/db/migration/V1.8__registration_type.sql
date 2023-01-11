alter table edition
  add column if not exists registration_type_id   bigint constraint fk79bnx9cv976m8inpogt902iah references registration_type;

alter table promotion
  add column if not exists vacancies integer;

alter table registration_type
  drop column if exists name,
  drop column if exists description,
  drop column if exists enabled,
  drop column if exists vacancies,
  drop column if exists certificate_id;

alter table registration
  drop column if exists confirmed,
  drop column if exists registration_type_id,
  add column if not exists price double precision not null,
  add column if not exists registration_date_time timestamp not null,
  add column if not exists promotion_id bigint constraint fkq6fk4us388a74cpkq3qil935i references promotion;

alter table tutored_registration
  drop column if exists registration_type_id,
  add column if not exists price double precision not null,
  add column if not exists registration_date_time timestamp not null;

alter table individual_registration
  drop column if exists payed;
