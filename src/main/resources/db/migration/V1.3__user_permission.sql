create table if not exists user_permission
(
  id
    bigserial
    constraint
      user_permission_pkey
      primary
        key,
  user_role
    integer
    not
      null,
  edition_id
    bigint
    constraint
      fk7ff9ss8o9r0ggmxm9pvasf2d2
      references
        edition,
  user_id
    bigint
    constraint
      fkn8ba4v3gvw1d82t3hofelr82t
      references
        users
);

alter table registration
  drop column user_role;


