create table if not exists space
(
  id             bigserial
  primary key,
  name           varchar(255) not null,
  institution_id bigint
  constraint fk15ox913i5nl1wxfjnx8e9gre6
  references institution,
  constraint uk1suklttyjmumr7exc3urr5dtb
  unique (name, institution_id)
  );

alter table edition
    alter column institution_id set not null;

alter table activity
    drop column if exists place,
    add column if not exists place_id bigint constraint fkki6c1f6evppj3442nr2r3irsr references space;
