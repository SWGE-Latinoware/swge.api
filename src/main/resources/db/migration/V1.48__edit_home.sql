create table if not exists edition_home
(
  id           bigserial
  primary key,
  home_content varchar,
  edition_id   bigint
  constraint fkm2re7fgydqkb1bd7mnp3yjox2
  references edition,
  language  varchar

);
