create table if not exists delete_request
(
  id                bigserial
  primary key,
  applicant_contact varchar,
  note              varchar,
  request_date      timestamp not null,
  request_type      integer   not null
);

create table if not exists exclusion
(
  id                      bigserial
  primary key,
  deadline_exclusion_date timestamp,
  effective_deletion_date timestamp,
  note                    varchar,
  registry_date           timestamp not null,
  return_date             timestamp,
  status                  integer default 0 not null,
  attachment_id           bigint
  constraint fk2fqivet3n7oxi8j9gqqv98ogy
  references file,
  delete_request_id       bigint    not null
  constraint fk1a3dq02ui0tephdyqsfmi7q9v
  references delete_request,
  dpo_id                  bigint
  constraint fkaef1bk33nmjx7dy5w1lk1afob
  references users,
  tutored_user_id         bigint
  constraint fk10bwd7tiew3ensi200g8wbu70
  references tutored_user,
  user_id                 bigint
  constraint fkpe63mpjrsfpojprq99mtroffr
  references users
  );
