package com.soyokra.sprival.app.controller.response;

import lombok.Data;
import java.util.List;

/**
 * RabbitMQ消费消息响应DTO
 *
 * @author sprival
 */
@Data
public class RabbitConsumeResponse {

    /**
     * 队列名称
     */
    private String queue;

    /**
     * 消费到的消息列表
     */
    private List<MessageItem> messages;

    /**
     * 消费数量
     */
    private Integer count;

    /**
     * 消息项
     */
    @Data
    public static class MessageItem {
        /**
         * 消息内容
         */
        private Object body;

        /**
         * 消息ID
         */
        private String messageId;

        /**
         * 路由键
         */
        private String routingKey;

        /**
         * 交换器
         */
        private String exchange;
    }
}

