create table if not exists voucher
(
    id           bigserial
        primary key,
    user_email   varchar(255) not null,
    voucher_hash varchar(255) not null,
    edition_id   bigint
        constraint fkgau1x2uk01dk2a6629kk4skbe
            references edition
);
