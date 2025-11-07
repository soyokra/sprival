package com.soyokra.sprival.app.controller.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * RabbitMQ发送消息请求DTO
 *
 * @author sprival
 */
@Data
public class RabbitSendRequest {

    /**
     * 交换器名称（可选，为空时直接发送到队列）
     */
    private String exchange;

    /**
     * 路由键（可选）
     */
    private String routingKey;

    /**
     * 队列名称（exchange为空时必填）
     */
    private String queue;

    /**
     * 消息内容
     */
    @NotNull(message = "消息内容不能为空")
    private Object message;
}

