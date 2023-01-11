alter table activity
  alter column name type varchar,
  alter column language type varchar,
  alter column place_url type varchar,
  alter column language_flag type varchar;

alter table caravan
  alter column city type varchar,
  alter column country type varchar,
  alter column name type varchar,
  alter column state type varchar;

alter table certificate
  alter column name type varchar;

alter table edition
  alter column description type varchar,
  alter column name type varchar,
  alter column short_name type varchar;

alter table feedback
  alter column title type varchar;

alter table file
  alter column format type varchar,
  alter column name type varchar;

alter table institution
  alter column city type varchar,
  alter column country type varchar,
  alter column name type varchar,
  alter column phone type varchar,
  alter column short_name type varchar,
  alter column state type varchar,
  alter column website type varchar,
  alter column cell_phone type varchar;

alter table language
  alter column flag type varchar,
  alter column iso_cod type varchar,
  alter column name type varchar,
  alter column translation type varchar;

alter table registration
  alter column user_registration_id type varchar;

alter table space
  alter column name type varchar;

alter table theme
  alter column name type varchar;

alter table track
  alter column name type varchar;

alter table tutored_registration
  alter column user_registration_id type varchar;

alter table tutored_user
  alter column cell_phone type varchar,
  alter column country type varchar,
  alter column name type varchar,
  alter column tag_name type varchar,
  alter column id_number type varchar,
  alter column other_needs type varchar;

alter table url
  alter column email type varchar,
  alter column url_fragment type varchar;

alter table users
  alter column cell_phone type varchar,
  alter column country type varchar,
  alter column name type varchar,
  alter column tag_name type varchar,
  alter column address_line1 type varchar,
  alter column address_line2 type varchar,
  alter column city type varchar,
  alter column email type varchar,
  alter column job_title type varchar,
  alter column password type varchar,
  alter column phone type varchar,
  alter column state type varchar,
  alter column zip_code type varchar,
  alter column github type varchar,
  alter column orcid type varchar,
  alter column linkedin type varchar,
  alter column website type varchar,
  alter column lattes type varchar,
  alter column other_needs type varchar,
  alter column github_id type varchar,
  alter column google_id type varchar;
