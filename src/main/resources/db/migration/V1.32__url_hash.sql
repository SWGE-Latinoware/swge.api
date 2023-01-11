ALTER TABLE url
ADD COLUMN if not exists hash integer;

ALTER TABLE certificate
ADD COLUMN if not exists allow_qr_code boolean default true not null;
