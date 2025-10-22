CREATE TABLE `order_pay`
(
    `order_id`      varchar(22) NOT NULL DEFAULT '' COMMENT '订单id',
    `trade_id`      varchar(20) NOT NULL DEFAULT '' COMMENT '合并支付id',
    `total_amount`  decimal(11, 2) unsigned DEFAULT NULL COMMENT '订单总金额',
    `pay_amount`    decimal(11, 2) unsigned DEFAULT NULL COMMENT '支付金额',
    `coupon_amount` decimal(11, 2) unsigned DEFAULT NULL COMMENT '优惠金额',
    `create_time`   datetime    NOT NULL COMMENT '创建时间',
    `update_time`   datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`order_id`),
    KEY             `idx_trade_id` (`trade_id`),
    KEY             `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4