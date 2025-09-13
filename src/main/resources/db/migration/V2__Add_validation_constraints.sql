-- Adicionar constraints de validação

-- Tabela categories
ALTER TABLE categories
    ALTER COLUMN name TYPE VARCHAR(100),
    ADD CONSTRAINT ck_categories_name_not_empty CHECK (LENGTH(TRIM(name)) > 0),
    ADD CONSTRAINT ck_categories_description_length CHECK (description IS NULL OR LENGTH(description) <= 255);

-- Tabela products
ALTER TABLE products
    ALTER COLUMN name TYPE VARCHAR(100),
    ADD CONSTRAINT ck_products_name_not_empty CHECK (LENGTH(TRIM(name)) > 0),
    ADD CONSTRAINT ck_products_description_length CHECK (description IS NULL OR LENGTH(description) <= 255),
    ADD CONSTRAINT ck_products_price_positive CHECK (price > 0),
    ALTER COLUMN category_id SET NOT NULL;

-- Criar índices - performance
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_categories_name ON categories(name);