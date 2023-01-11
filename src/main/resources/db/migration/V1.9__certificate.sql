alter table certificate
  drop column if exists background_image,
  add column if not exists background_image_id bigint not null constraint certificate_file_id_fk references file;

alter table dynamic_content
  alter column content type varchar using content::varchar;
