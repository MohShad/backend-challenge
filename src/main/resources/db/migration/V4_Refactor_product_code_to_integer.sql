-- Migration V4: Refatorar código do produto de String para Integer

-- Add new integer column temporarily
ALTER TABLE products ADD COLUMN code_new INTEGER;

-- Convert existing codes from PROD-XXX to XXX format
UPDATE products SET code_new = CAST(SUBSTRING(code FROM 6) AS INTEGER) WHERE code IS NOT NULL AND code LIKE 'PROD-%';

-- For codes that don't follow PROD-XXX pattern, generate sequential numbers
UPDATE products SET code_new = id WHERE code_new IS NULL;

-- Drop old column and rename new one
ALTER TABLE products DROP COLUMN code;
ALTER TABLE products RENAME COLUMN code_new TO code;

-- Add unique constraint for the new integer code
ALTER TABLE products ADD CONSTRAINT uk_products_code_integer UNIQUE (code);