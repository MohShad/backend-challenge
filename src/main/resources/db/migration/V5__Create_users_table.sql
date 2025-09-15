-- Criar tabela usuário
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(10) NOT NULL CHECK (role IN ('admin', 'user'))
);

-- Criar índices
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Usuário admin inicial
-- Hash da senha 'KMbT%5wT*R!46i@@YHqx', usando BCrypt
INSERT INTO users (name, email, password, role) VALUES
('Admin User', 'contato@simplesdental.com', '$2a$12$.5DLUEaC/df00pZ7tIO1vuZgDeOQvaklcbC42QPl5b5Tqy9iVUsKK', 'admin');