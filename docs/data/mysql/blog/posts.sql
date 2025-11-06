CREATE TABLE posts
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200)        NOT NULL,
    slug        VARCHAR(200) UNIQUE NOT NULL,
    content     LONGTEXT            NOT NULL,
    excerpt     VARCHAR(500) DEFAULT NULL,
    status      ENUM('draft', 'published', 'archived') DEFAULT 'draft',
    author_id   INT                 NOT NULL,
    category_id INT                 NOT NULL,
    view_count  INT          DEFAULT 0,
    created_at  DATE         DEFAULT NULL,
    updated_at  DATE         DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 索引优化
CREATE INDEX idx_slug ON posts (slug);
CREATE INDEX idx_status ON posts (status);
CREATE INDEX idx_author_id ON posts (author_id);
CREATE INDEX idx_category_id ON posts (category_id);