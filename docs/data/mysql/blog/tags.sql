CREATE TABLE tags
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50) UNIQUE NOT NULL,
    slug       VARCHAR(50) UNIQUE NOT NULL,
    created_at DATE DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 索引优化
CREATE INDEX idx_slug ON tags (slug);