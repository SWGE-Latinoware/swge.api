create or replace function change_sys_user() returns trigger
  language plpgsql as
$replaceuser$
begin
  if new.responsible_user != '0' then
    new.responsible_user := (select id from users where email = new.responsible_user);
  end if;

  WITH subquery AS (SELECT id,
                           email
                    FROM users)
  UPDATE audits
  SET responsible_user = 0
  FROM subquery
  WHERE audits.responsible_user = 'ADMIN'
     OR audits.responsible_user IS NULL;

  WITH subquery AS (SELECT id,
                           email
                    FROM users)
  UPDATE audits
  SET responsible_user = subquery.id
  FROM subquery
  WHERE audits.responsible_user = subquery.email;

  return new;
end;
$replaceuser$;

create or replace trigger change_user
  before insert
  on public.audits
  for each row
execute procedure change_sys_user();
