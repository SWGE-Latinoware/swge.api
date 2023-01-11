create table if not exists certificate
(
  id                     bigserial
  constraint certificate_pkey
  primary key,
  availability_date_time timestamp    not null,
  background_image       varchar(255) not null,
  name                   varchar(255) not null
  );

create table if not exists dynamic_content
(
  id             bigserial
  constraint dynamic_content_pkey
  primary key,
  bold           integer      not null,
  content        varchar(255) not null,
  font_size      integer      not null,
  italic         integer      not null,
  underlined     integer      not null,
  x              integer      not null,
  y              integer      not null,
  certificate_id bigint
  constraint fkp0sm74007ohx65jbv5r6s92s4
  references certificate
  );

create table if not exists file
(
  id     bigserial
  constraint file_pkey
  primary key,
  format varchar(255),
  name   varchar(255) not null
  );

create table if not exists institution
(
  id         bigserial
  constraint institution_pkey
  primary key,
  city       varchar(255),
  country    varchar(255),
  name       varchar(255) not null,
  phone      varchar(255),
  short_name varchar(255),
  state      varchar(255),
  website    varchar(255)
  );

create table if not exists language
(
  id          bigserial
  constraint language_pkey
  primary key,
  flag        varchar(255),
  iso_cod     varchar(255) not null,
  name        varchar(255) not null,
  translation text         not null
  );

create table if not exists registration_type
(
  id                bigserial
  constraint registration_type_pkey
  primary key,
  description       varchar(255),
  enabled           boolean          not null,
  final_date_time   timestamp        not null,
  initial_date_time timestamp        not null,
  name              varchar(255)     not null,
  price             double precision not null,
  vacancies         integer          not null,
  certificate_id    bigint
  constraint fk7bhdx9j82re4584n6mrsg9k4e
  references certificate
  );

create table if not exists promotion
(
  id                   bigserial
  constraint promotion_pkey
  primary key,
  final_date_time      timestamp        not null,
  initial_date_time    timestamp        not null,
  price                double precision not null,
  registration_type_id bigint
  constraint fk5e5b73gyqo0kvgyvcv2yh66ed
  references registration_type
);

create table if not exists sub_event
(
  id           bigserial
  constraint sub_event_pkey
  primary key,
  name         varchar(255)     not null,
  price        double precision not null,
  vacancies    integer          not null,
  sub_event_id bigint
  constraint fkrb379as6dhupm2je1294e1p54
  references certificate
  );

create table if not exists theme
(
  id            bigserial
  constraint theme_pkey
  primary key,
  color_palette varchar      not null,
  name          varchar(255) not null
  constraint uk_kos4rdub1av4d5wt6wocsdb7t
  unique,
  type          integer      not null
  );

create table if not exists edition
(
  id                     bigserial
  constraint edition_pkey
  primary key,
  description            varchar(255),
  enabled                boolean      not null,
  final_date             timestamp    not null,
  initial_date           timestamp    not null,
  name                   varchar(255) not null,
  short_name             varchar(255) not null,
  type                   integer      not null,
  year                   integer      not null,
  default_dark_theme_id  bigint
  constraint fkr6e7jubtfcuq062prioxn2f2b
  references theme,
  default_language_id    bigint
  constraint fk2np6a6odfm1816es3gb1g5x36
  references language,
  default_light_theme_id bigint
  constraint fknj4v1tegmkcx560fb99ab8ptb
  references theme,
  institution_id         bigint
  constraint fkb72sklwokqv6xw6b2si7hlkgj
  references institution,
  logo_id                bigint
  constraint fk1owvv75b66olwmue35sm7yew
  references file
  );

create table if not exists edition_language
(
  id          bigserial
  constraint edition_language_pkey
  primary key,
  enabled     boolean not null,
  edition_id  bigint
  constraint fkmv3ec2gsq8bsiqm5mv1dovntl
  references edition,
  language_id bigint
  constraint fkh1x6fyrb42mgvw8fn8w2tn84d
  references language
);

create table if not exists track
(
  id           bigserial
  constraint track_pkey
  primary key,
  description  varchar,
  final_date   timestamp    not null,
  initial_date timestamp    not null,
  name         varchar(255) not null,
  edition_id   bigint
  constraint fkr8ksk883jgeqy11rp52kjix7j
  references edition
  );

create table if not exists tutored_user
(
  id         bigserial
  constraint tutored_user_pkey
  primary key,
  birth_date timestamp,
  cell_phone varchar(255),
  country    varchar(255) not null,
  gender     integer,
  name       varchar(255) not null
  constraint uk_dvqpeepvc7pkngfx61boyabaa
  unique,
  tag_name   varchar(255) not null,
  id_number  varchar(255) not null
  constraint uk_84bl6ub5y4mjad6gc9hyag3ty
  unique
  );

create table if not exists tutored_registration
(
  id                   bigserial
  constraint tutored_registration_pkey
  primary key,
  payed                boolean      not null,
  user_registration_id varchar(255) not null,
  edition_id           bigint
  constraint fk5ej0knnoo6l3td8n2les92krp
  references edition,
  registration_type_id bigint
  constraint fkn2gabeux61q8w9053of7ateu5
  references registration_type,
  tutored_user_id      bigint
  constraint fkcy7w8cq3ck0ini9324ow151ge
  references tutored_user
  );

create table if not exists users
(
  id                  bigserial
  constraint users_pkey
  primary key,
  birth_date          timestamp,
  cell_phone          varchar(255),
  country             varchar(255) not null,
  gender              integer,
  name                varchar(255) not null
  constraint uk_3g1j96g94xpk3lpxl2qbl985x
  unique,
  tag_name            varchar(255) not null,
  address_line1       varchar(255),
  address_line2       varchar(255),
  admin               boolean      not null,
  city                varchar(255),
  confirmed           boolean      not null,
  email               varchar(255) not null
  constraint uk_6dotkott2kjsp8vw4d0m25fb7
  unique,
  email_communication boolean      not null,
  enabled             boolean      not null,
  job_title           varchar(255),
  password            varchar(255) not null,
  phone               varchar(255),
  state               varchar(255),
  zip_code            varchar(255),
  institution_id      bigint
  constraint fkes3l5tviwmnu2d0gy350kdfht
  references institution
  );

create table if not exists caravan
(
  id             bigserial
  constraint caravan_pkey
  primary key,
  city           varchar(255),
  country        varchar(255)     not null,
  name           varchar(255)     not null,
  payed          boolean          not null,
  price          double precision not null,
  state          varchar(255),
  type           integer          not null,
  vacancies      integer          not null,
  coordinator_id bigint
  constraint fkhckq4mxckru6wtls2vu21c8uj
  references users,
  edition_id     bigint
  constraint fkcut2jbelclfyixyt6gur0jybm
  references edition,
  institution_id bigint           not null
  constraint fklbn5q5g4x02qe5yw1kiwd2ib0
  references institution
  );

create table if not exists caravan_enrollment
(
  id         bigserial
  constraint caravan_enrollment_pkey
  primary key,
  accepted   boolean not null,
  payed      boolean not null,
  confirmed  boolean not null,
  caravan_id bigint
  constraint fkfdjkc2a2uqs7ru7bejej4t91l
  references caravan,
  user_id    bigint
  constraint fkb2ea5uqj0jwahji5bls73axwo
  references users
);

create table if not exists caravan_tutored_enrollment
(
  id              bigserial
  constraint caravan_tutored_enrollment_pkey
  primary key,
  accepted        boolean not null,
  payed           boolean not null,
  caravan_id      bigint
  constraint fk3p37qmqopre3efila49poobxs
  references caravan,
  tutored_user_id bigint
  constraint fkf7v5dguc8ogj5eu76u3svmf5g
  references tutored_user
);

create table if not exists notice
(
  id          bigserial
  constraint notice_pkey
  primary key,
  date_time   timestamp not null,
  description varchar   not null,
  caravan_id  bigint
  constraint fk2cvsivf5tl7068jyfmrd0y11c
  references caravan
);

create table if not exists registration
(
  id                   bigserial
  constraint registration_pkey
  primary key,
  payed                boolean      not null,
  user_registration_id varchar(255) not null,
  confirmed            boolean      not null,
  user_role            integer      not null,
  edition_id           bigint
  constraint fkitsr494h7tec35j5fc1jp736u
  references edition,
  registration_type_id bigint
  constraint fk3ug06wanm45ymel8a8m16xhmq
  references registration_type,
  user_id              bigint
  constraint fkkyuphiynxwt1mtlfsptc991sc
  references users
  );

create table if not exists individual_registration
(
  id              bigserial
  constraint individual_registration_pkey
  primary key,
  payed           boolean not null,
  registration_id bigint
  constraint fkre93xpedqnkaprsa6kk52kco5
  references registration,
  sub_event_id    bigint
  constraint fk6dh70anmyksh1ep7l57tokuc9
  references sub_event
);

create table if not exists url
(
  id           bigserial
  constraint url_pkey
  primary key,
  email        varchar(255),
  type         integer      not null,
  url_fragment varchar(255) not null,
  user_id      bigint
  constraint fkt9oxwarfpcswxu1l63hxawfsq
  references users
  );

