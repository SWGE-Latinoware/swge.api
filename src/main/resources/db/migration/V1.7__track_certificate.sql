ALTER TABLE track
  ADD COLUMN if not exists attendee_certificate_id bigint
    constraint fkpauad2pe86e4rmt5u5humet4h
      references certificate,

  ADD COLUMN if not exists speaker_certificate_id  bigint
    constraint fkg48scllwwat2e60tj6ati6b8x
      references certificate;
