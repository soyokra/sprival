package com.soyokra.sprival.runner.mq;

import com.soyokra.sprival.support.mq.MqFactory;
import com.soyokra.sprival.support.mq.Consumer;
import com.soyokra.sprival.support.mq.IAdmin;
import com.soyokra.sprival.support.mq.IConsumer;
import com.soyokra.sprival.support.mq.Queue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ConsumerRunner implements ApplicationRunner {

    @Autowired
    MqFactory mqFactory;

    @Override
    public void run(ApplicationArguments args) {

        try {
            IAdmin adminImpl = mqFactory.admin(MqFactory.Driver.RABBITMQ);
            IConsumer consumerImpl = mqFactory.consumer(MqFactory.Driver.RABBITMQ);

            List<Queue> queueList = new ArrayList<>();
            queueList.add(Queue.valueOf("test_a"));
            queueList.add(Queue.valueOf("test_b"));
            queueList.add(Queue.valueOf("test_c"));
            queueList.add(Queue.valueOf("test_delay_5", 5));
            queueList.add(Queue.valueOf("test_delay_10", 10));
            queueList.add(Queue.valueOf("test_delay_30", 30));
            for (Queue queue : queueList) {
                adminImpl.declareQueue(queue);
            }

            List<Consumer> consumerList = new ArrayList<>();
            consumerList.add(Consumer.valueOf("test_a", Queue.valueOf("test_a")));
            consumerList.add(Consumer.valueOf("test_b", Queue.valueOf("test_b")));
            consumerList.add(Consumer.valueOf("test_c", Queue.valueOf("test_c")));
            consumerList.add(Consumer.valueOf("test_delay_5", Queue.valueOf("test_delay_5")));
            consumerList.add(Consumer.valueOf("test_delay_10", Queue.valueOf("test_delay_10")));
            consumerList.add(Consumer.valueOf("test_delay_30", Queue.valueOf("test_delay_30")));
            consumerImpl.listenerUp(consumerList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        try {
            IConsumer consumerImpl2 = mqFactory.consumer(MqFactory.Driver.KAFKA);
            consumerImpl2.listenerUp(null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
