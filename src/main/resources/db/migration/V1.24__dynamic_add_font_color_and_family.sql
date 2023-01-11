ALTER TABLE dynamic_content
  drop column if exists color_font,
  add column if not exists font_color varchar,
  add column if not exists font_family varchar;

UPDATE dynamic_content
SET font_color = '#000000'
FROM dynamic_content dc
WHERE dc.font_color is null;

UPDATE dynamic_content
SET font_family = 'Courier'
FROM dynamic_content dc
WHERE dc.font_family is null;

ALTER TABLE dynamic_content
  ALTER COLUMN font_color SET NOT NULL,
  ALTER COLUMN font_family SET NOT NULL;
