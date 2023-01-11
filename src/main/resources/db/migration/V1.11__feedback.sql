create table if not exists feedback
(
    id          bigserial
        primary key,
    description varchar,
    status      integer      not null,
    title       varchar(255) not null,
    user_id     bigint
        constraint fkpwwmhguqianghvi1wohmtsm8l
            references users,
    file_id     bigint
        constraint fk2o9sei0c0yfy19ouwk1xcpcpy
            references file
);

