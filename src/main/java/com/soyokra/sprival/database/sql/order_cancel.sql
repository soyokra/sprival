CREATE TABLE `order_cancel`
(
    `order_id`      varchar(22) NOT NULL DEFAULT '' COMMENT '订单号',
    `order_type`    varchar(50) NOT NULL DEFAULT '' COMMENT '订单类型',
    `cancel_no`     int(11) unsigned NOT NULL DEFAULT '0' COMMENT '取消原因类型',
    `cancel_reason` text COMMENT '取消原因',
    `create_time`   datetime    NOT NULL COMMENT '创建时间',
    `update_time`   datetime    NOT NULL COMMENT '更新时间'
    PRIMARY KEY (`order_id`),
    KEY             `cancel_no` (`cancel_no`),
    KEY             `create_time` (`create_time`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='订单取消表'