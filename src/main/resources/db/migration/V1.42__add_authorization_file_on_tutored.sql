ALTER TABLE tutored_user
  add column if not exists authorization_id bigint
  constraint fkce1zwqjhm4lfcp9tkuabk1lce references file;
