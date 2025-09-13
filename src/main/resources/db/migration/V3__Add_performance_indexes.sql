-- Índice composto para consultas combinadas frequentes
CREATE INDEX idx_products_status_category ON products(status, category_id);

-- Restrições exclusivas
ALTER TABLE products ADD CONSTRAINT uk_products_code UNIQUE (code);
ALTER TABLE categories ADD CONSTRAINT uk_categories_name UNIQUE (name);