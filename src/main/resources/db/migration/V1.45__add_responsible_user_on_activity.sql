ALTER TABLE activity
ADD IF NOT EXISTS responsible_id bigint constraint fkhfy3njbhdiw2sucaxyt8ijxts references users;
