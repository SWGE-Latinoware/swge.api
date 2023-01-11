ALTER TABLE dynamic_content
  DROP COLUMN if exists bold,
  DROP COLUMN if exists italic,
  DROP COLUMN if exists underlined;
