create table if not exists activity
(
  id
                          bigserial
    constraint
      activity_pkey
      primary
        key,
  description
                          varchar,
  name
                          varchar(255)     not null,
  language                varchar(255)     not null,
  presentation_type       integer          not null,
  price                   double precision not null,
  type                    integer          not null,
  vacancies               integer          not null,
  workload                double precision not null,
  attendee_certificate_id bigint
    constraint fk6keejjwltwxiyl4oeewqeev4c
      references certificate,
  speaker_certificate_id  bigint
    constraint fkq9snc0fa6ddqcnicsbucw44vq
      references certificate,
  track_id                bigint
    constraint fk6p4700qqxvqks51f3pf40wtgr
      references track
);

alter table certificate
  add column if not exists edition_id bigint
    constraint fkrkudq3s9nydfmnhtqou57n10o
      references edition;

alter table dynamic_content
  alter
    column bold type boolean USING bold::boolean,
alter
column bold
set not null ,
alter
column italic type boolean USING bold::boolean,
  alter
column italic
set not null ,
alter
column underlined type boolean USING bold::boolean,
  alter
column underlined
set not null;

alter table individual_registration
  rename column sub_event_id to activity_id;

drop table if exists sub_event cascade;

create table if not exists speaker_activity
(
  id
    bigserial
    constraint
      speaker_activity_pkey
      primary
        key,
  activity_id
    bigint
    constraint
      fksby0swauq77qh30t5hsmek35d
      references
        activity,
  speaker_id
    bigint
    constraint
      fk168gtv2cp52pg4lni4e8315lh
      references
        users
);


