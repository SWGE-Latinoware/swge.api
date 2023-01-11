DO $$
BEGIN
  IF EXISTS(SELECT *
    FROM information_schema.columns
    WHERE table_name='promotion' and column_name='price')
    THEN
      ALTER TABLE "public"."promotion" RENAME COLUMN "price" TO "percentage";
  END IF;
END $$;
