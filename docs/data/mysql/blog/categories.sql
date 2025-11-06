CREATE TABLE categories
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100)        NOT NULL,
    slug        VARCHAR(100) UNIQUE NOT NULL,
    parent_id   INT          DEFAULT NULL,
    description VARCHAR(255) DEFAULT NULL,
    created_at  DATE    DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 索引优化
CREATE INDEX idx_slug ON categories (slug);
CREATE INDEX idx_parent_id ON categories (parent_id);