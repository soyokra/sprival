package com.soyokra.sprival.app.service;

import com.rabbitmq.client.Channel;
import com.soyokra.sprival.app.controller.request.RabbitSendRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * RabbitMQ服务类
 *
 * @author sprival
 */
@Slf4j
@Service
public class RabbitService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到RabbitMQ
     *
     * @param request 发送请求
     * @return 是否成功
     */
    public boolean sendMessage(RabbitSendRequest request) {
        try {
            if (request.getExchange() != null && !request.getExchange().isEmpty()) {
                // 通过交换器发送
                if (request.getRoutingKey() != null && !request.getRoutingKey().isEmpty()) {
                    log.info("发送消息到交换器，exchange: {}, routingKey: {}", request.getExchange(), request.getRoutingKey());
                    rabbitTemplate.convertAndSend(request.getExchange(), request.getRoutingKey(), request.getMessage());
                } else {
                    log.info("发送消息到交换器，exchange: {}", request.getExchange());
                    rabbitTemplate.convertAndSend(request.getExchange(), request.getMessage());
                }
            } else if (request.getQueue() != null && !request.getQueue().isEmpty()) {
                // 直接发送到队列
                log.info("发送消息到队列，queue: {}", request.getQueue());
                rabbitTemplate.convertAndSend(request.getQueue(), request.getMessage());
            } else {
                log.warn("发送消息失败：exchange和queue不能同时为空");
                return false;
            }
            log.info("消息发送成功");
            return true;
        } catch (Exception e) {
            log.error("消息发送失败", e);
            return false;
        }
    }

    /**
     * RabbitMQ消息消费者（自动消费）
     *
     * @param request 消息内容（Object类型，可以是Map或自定义对象）
     * @param message 消息对象
     * @param channel 通道对象
     */
    @RabbitListener(queues = "${rabbitmq.post.queue}")
    public void postMessageConsumer(Object request, Message message, Channel channel) {
        log.info("接收到消息，request={}", request);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            // 处理业务逻辑
            handleMessage(request, message);
            
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            log.info("消息处理成功并已确认，deliveryTag: {}", deliveryTag);
        } catch (Exception e) {
            log.error("消息处理失败，deliveryTag: {}", deliveryTag, e);
            try {
                // 拒绝消息，不重新入队（避免无限重试）
                channel.basicNack(deliveryTag, false, false);
                log.warn("消息已拒绝，deliveryTag: {}", deliveryTag);
            } catch (IOException ioException) {
                log.error("拒绝消息失败，deliveryTag: {}", deliveryTag, ioException);
            }
        }
    }

    /**
     * 处理消息业务逻辑
     *
     * @param request 消息内容（Object类型）
     * @param message 消息对象
     */
    private void handleMessage(Object request, Message message) {
        // 这里实现具体的业务逻辑
        log.info("处理消息，messageId: {}, routingKey: {}, requestType: {}", 
                message.getMessageProperties().getMessageId(),
                message.getMessageProperties().getReceivedRoutingKey(),
                request.getClass().getName());
        // TODO: 实现具体的业务处理逻辑
    }
}

