CREATE TABLE comments
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    post_id    INT  NOT NULL,
    parent_id  INT       DEFAULT NULL,
    author_id  INT       DEFAULT NULL,
    content    TEXT NOT NULL,
    status     ENUM('pending', 'approved', 'spam') DEFAULT 'pending',
    created_at DATE DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 索引优化
CREATE INDEX idx_post_id ON comments (post_id);
CREATE INDEX idx_parent_id ON comments (parent_id);
CREATE INDEX idx_status ON comments (status);