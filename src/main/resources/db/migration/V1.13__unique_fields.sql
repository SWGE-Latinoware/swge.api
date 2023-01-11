alter table institution
    drop constraint if exists institution_name_unique_constraint,
    ADD CONSTRAINT institution_name_unique_constraint UNIQUE (name);

alter table edition
    drop constraint if exists edition_name_unique_constraint,
    ADD CONSTRAINT edition_name_unique_constraint UNIQUE (name),
    drop constraint if exists edition_short_name_unique_constraint,
    ADD CONSTRAINT edition_short_name_unique_constraint UNIQUE (short_name),
    drop constraint if exists edition_year_unique_constraint,
    ADD CONSTRAINT edition_year_unique_constraint UNIQUE (year);

alter table track
   drop constraint if exists ukka8t46g9urorakigyluom1ppo,
   ADD CONSTRAINT ukka8t46g9urorakigyluom1ppo UNIQUE (name, edition_id);

alter table caravan
   drop constraint if exists ukqmhqxc71d7xu4xrc2xji3l7dv,
   ADD CONSTRAINT ukqmhqxc71d7xu4xrc2xji3l7dv UNIQUE (name, edition_id);

alter table certificate
   drop constraint if exists ukc6s9lqn0c4qdjb2ll0ay32pss,
   ADD CONSTRAINT ukc6s9lqn0c4qdjb2ll0ay32pss UNIQUE (name, edition_id);

alter table activity
   add column if not exists edition_id bigint constraint fkdfp8j625fovlcv78atuf6cb92 references edition;

update activity a
  set edition_id = t.edition_id
  from track t
  where a.track_id = t.id;
