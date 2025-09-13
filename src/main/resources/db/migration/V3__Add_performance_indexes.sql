-- Índices de desempenho para consultas comuns
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_name_text ON products(name);
CREATE INDEX idx_categories_name ON categories(name);

-- Índice composto para consultas combinadas frequentes
CREATE INDEX idx_products_status_category ON products(status, category_id);

-- Restrições exclusivas
ALTER TABLE products ADD CONSTRAINT uk_products_code UNIQUE (code);
ALTER TABLE categories ADD CONSTRAINT uk_categories_name UNIQUE (name);