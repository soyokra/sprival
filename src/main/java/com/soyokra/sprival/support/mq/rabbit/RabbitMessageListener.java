package com.soyokra.sprival.support.mq.rabbit;

import com.rabbitmq.client.Channel;
import com.soyokra.sprival.dao.master.provider.TestRabbitProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class RabbitMessageListener implements ChannelAwareMessageListener {
    @Autowired
    TestRabbitProvider testRabbitProvider;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String testId = "";
        try {
            testId = new String(message.getBody());
            testRabbitProvider.incrCCount(testId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // ack
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            } catch (IOException ioe) {
                log.error(ioe.getMessage(), ioe);
            }
        }
    }
}
