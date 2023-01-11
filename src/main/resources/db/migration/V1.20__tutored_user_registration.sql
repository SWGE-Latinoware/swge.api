create table if not exists tutored_individual_registration
(
  id              bigserial
  primary key,
  activity_id     bigint
  constraint fkndnmhl98x65e7mfnuedfwedhp
  references activity,
  registration_id bigint
  constraint fkemn13osgng6jjaehtrwjubqul
  references tutored_registration
);
