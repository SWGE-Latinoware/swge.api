ALTER TABLE tutored_user
ADD COLUMN IF NOT EXISTS reviewer_id bigint constraint fkb9plwbx9g3h11vr2ysqvjfca0 references users;

ALTER TABLE promotion
ADD COLUMN IF NOT EXISTS is_voucher boolean default false not null;
