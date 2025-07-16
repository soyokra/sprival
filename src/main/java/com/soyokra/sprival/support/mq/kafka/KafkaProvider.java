package com.soyokra.sprival.support.mq.kafka;

import com.soyokra.sprival.support.mq.*;
import com.soyokra.sprival.support.mq.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;
import java.util.*;

@Service
public class KafkaProvider implements IAdmin, IBroker, IConsumer {
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ConsumerFactory<String, String> consumerFactory;



    @Override
    public void declareQueue(Queue queue) {

    }

    @Override
    public void publish(Queue queue, String message) {
        kafkaTemplate.send(queue.getQueueName(), message);
    }

    @Override
    public void listenerUp(List<Consumer> consumerList) {
        DefaultMessageHandlerMethodFactory messageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory();

        ConcurrentKafkaListenerContainerFactory<String, String> listenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        listenerContainerFactory.setConsumerFactory(consumerFactory);
        // listenerContainerFactory.setMessageConverter(new StringJsonMessageConverter());


        KafkaListenerEndpointRegistry endpointRegistry = new KafkaListenerEndpointRegistry();

        MethodKafkaListenerEndpoint<String, Object> endpoint = new MethodKafkaListenerEndpoint<>();
        endpoint.setTopicPartitions();
        endpoint.setTopics("test");
        endpoint.setId("test");
        endpoint.setGroupId("test");
        endpoint.setMessageHandlerMethodFactory(messageHandlerMethodFactory);

        // 反射获取消息处理方法
        Method method = ReflectionUtils.findMethod(KafkaMessageListener.class, "handleMessage", Object.class);
        endpoint.setBean(new KafkaMessageListener());
        endpoint.setMethod(method);
        endpointRegistry.registerListenerContainer(endpoint, listenerContainerFactory, false);

        endpointRegistry.getListenerContainer("test").start();

    }

    @Override
    public void listenerDown(List<Consumer> consumerList) {
        listenerHandler(consumerList);
    }

    protected void listenerHandler(List<Consumer> consumerList) {

    }

    protected void listenerAutoClose(Set<String> consumerSet) {

    }

    private String[] getQueueNames(List<Queue> queueList) {
        List<String> queueNameList = new ArrayList<>();
        for (Queue queue : queueList) {
            queueNameList.add(queue.getQueueName());
        }
        return queueNameList.toArray(new String[0]);
    }
}
