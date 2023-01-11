DO $$
BEGIN
  IF EXISTS(SELECT *
    FROM information_schema.columns
    WHERE table_name='registration' and column_name='price')
    THEN
      ALTER TABLE "public"."registration" ADD COLUMN IF NOT EXISTS "original_price" double precision;
      UPDATE "public"."registration" SET "original_price" = rd."price" FROM registration rd WHERE rd.original_price is null;
      ALTER TABLE "public"."registration" RENAME COLUMN "price" TO "final_price";
      ALTER TABLE registration ALTER COLUMN original_price SET NOT NULL;
  END IF;
  IF EXISTS(SELECT *
    FROM information_schema.columns
    WHERE table_name='tutored_registration' and column_name='price')
    THEN
      ALTER TABLE "public"."tutored_registration" ADD COLUMN IF NOT EXISTS "original_price" double precision;
      UPDATE "public"."tutored_registration" SET "original_price" = rd."price" FROM tutored_registration rd WHERE rd.original_price is null;
      ALTER TABLE "public"."tutored_registration" RENAME COLUMN "price" TO "final_price";
      ALTER TABLE tutored_registration ALTER COLUMN original_price SET NOT NULL;
  END IF;
END $$;
