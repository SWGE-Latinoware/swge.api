create table if not exists  public.audits
(
    id               bigserial
        primary key,
    action           integer      not null,
    created_date     timestamp    not null,
    data_id          bigint,
    new_data         varchar not null,
    responsible_user varchar not null,
    table_name       varchar not null
);
