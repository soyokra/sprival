CREATE TABLE `order_trade_tbl`
(
    `trade_id`         varchar(20) NOT NULL DEFAULT '' COMMENT '合并支付Id',
    `trade_sn`         varchar(40)          DEFAULT NULL COMMENT '交易sn',
    `total_amount`     decimal(10, 2) unsigned    DEFAULT '0.00' COMMENT '总金额',
    `pay_amount`       decimal(10, 2) unsigned DEFAULT NULL COMMENT '支付金额',
    `coupon_amount`    decimal(10, 2) unsigned     DEFAULT NULL COMMENT '优惠金额',
    `status_no`        tinyint(3) unsigned DEFAULT '0' COMMENT '业务状态',
    `status_desc`      varchar(50)         DEFAULT NULL COMMENT '业务状态描述',
    `pay_time`         datetime             DEFAULT NULL COMMENT '支付时间',
    `pay_url`          varchar(500)         DEFAULT NULL COMMENT '支付链接',
    `pay_timeout_time` datetime             DEFAULT NULL COMMENT '支付超时时间',
    `idempotent_id`    varchar(50)          DEFAULT NULL COMMENT '幂等Id',
    `create_time`      datetime    NOT NULL COMMENT '添加时间',
    `update_time`      datetime    NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`trade_id`),
    KEY                `trade_sn` (`trade_sn`),
    KEY                `status_no` (`status_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单交易表'