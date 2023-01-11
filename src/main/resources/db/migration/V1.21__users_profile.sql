ALTER TABLE users
    add column if not exists user_profile_id bigint
        constraint fkbu7slcdby8ytno2pvrvjm6ety references file;
