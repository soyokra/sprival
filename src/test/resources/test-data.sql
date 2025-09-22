-- 测试数据
-- 插入测试用户
INSERT INTO sys_user (id, username, password, email, phone, status) VALUES
(1, 'testuser1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'test1@example.com', '13800138001', 'ACTIVE'),
(2, 'testuser2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'test2@example.com', '13800138002', 'ACTIVE'),
(3, 'testuser3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'test3@example.com', '13800138003', 'INACTIVE');

-- 插入测试产品
INSERT INTO sys_product (id, name, description, price, stock, status) VALUES
(1, '测试产品1', '这是一个测试产品', 99.99, 100, 'ACTIVE'),
(2, '测试产品2', '这是另一个测试产品', 199.99, 50, 'ACTIVE'),
(3, '测试产品3', '这是第三个测试产品', 299.99, 0, 'INACTIVE');

-- 插入测试订单
INSERT INTO sys_order (id, order_no, user_id, total_amount, status) VALUES
(1, 'ORDER001', 1, 99.99, 'PENDING'),
(2, 'ORDER002', 1, 199.99, 'COMPLETED'),
(3, 'ORDER003', 2, 299.99, 'CANCELLED');

-- 插入测试订单详情
INSERT INTO sys_order_item (id, order_id, product_id, quantity, price) VALUES
(1, 1, 1, 1, 99.99),
(2, 2, 2, 1, 199.99),
(3, 3, 3, 1, 299.99);
