CREATE TABLE post_tags
(
    post_id INT NOT NULL,
    tag_id  INT NOT NULL,
    PRIMARY KEY (post_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 索引优化
CREATE INDEX idx_post_tags_tag_id ON post_tags (tag_id);