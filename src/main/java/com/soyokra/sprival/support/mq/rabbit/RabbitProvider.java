package com.soyokra.sprival.support.mq.rabbit;

import com.soyokra.sprival.support.mq.*;
import com.soyokra.sprival.support.mq.Queue;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RabbitProvider implements IAdmin, IBroker, IConsumer {
    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    RabbitHelper rabbitHelper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    RabbitMessageListener rabbitMessageListener;


    protected static Map<String, SimpleMessageListenerContainer> CONSUMER_LISTENER_MAP = new ConcurrentHashMap<>();

    @Override
    public void declareQueue(Queue queue) {
        String queueName = queue.getQueueName();
        String delayQueueName = rabbitHelper.buildDelayName(queueName);
        FanoutExchange exchange = rabbitHelper.buildExchange(queueName);
        org.springframework.amqp.core.Queue driverQueue = rabbitHelper.buildQueue(queueName);
        Binding binding = rabbitHelper.buildBinding(driverQueue, exchange);

        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(driverQueue);
        amqpAdmin.declareBinding(binding);

        if (Objects.nonNull(queue.getDelayTime()) && queue.getDelayTime() > 0) {
            FanoutExchange delayExchange = rabbitHelper.buildExchange(delayQueueName);
            org.springframework.amqp.core.Queue delayQueue = rabbitHelper.buildDelayQueue(queueName, delayQueueName, queue.getDelayTime());
            Binding delayBinding = rabbitHelper.buildBinding(delayQueue, delayExchange);
            amqpAdmin.declareExchange(delayExchange);
            amqpAdmin.declareQueue(delayQueue);
            amqpAdmin.declareBinding(delayBinding);
        }
    }

    @Override
    public void publish(Queue queue, String content) {
        String queueName = queue.getQueueName();
        String delayQueueName = rabbitHelper.buildDelayName(queueName);
        MessageProperties messageProperties = new MessageProperties();
        if (Objects.nonNull(queue.getDelayTime()) && queue.getDelayTime() > 0) {
            // 延迟队列
            Message message = new Message(content.getBytes(), messageProperties);
            rabbitTemplate.convertAndSend(delayQueueName, "",  message);
        } else {
            // 正常队列
            Message message = new Message(content.getBytes(), messageProperties);
            rabbitTemplate.convertAndSend(queueName, "",  message);
        }
    }

    @Override
    public void listenerUp(List<Consumer> consumerList) {
       listenerHandler(consumerList);
    }

    @Override
    public void listenerDown(List<Consumer> consumerList) {
        listenerHandler(consumerList);
    }

    protected void listenerHandler(List<Consumer> consumerList) {
        if(! CollectionUtils.isEmpty(consumerList)) {
            Set<String> consumerSet = new HashSet<>();
            for (Consumer consumer : consumerList) {
                String consumerName = consumer.getConsumerName();
                consumerSet.add(consumerName);

                if(! CONSUMER_LISTENER_MAP.containsKey(consumerName)) {
                    SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer(connectionFactory);

                    listenerContainer.setExposeListenerChannel(true);
                    listenerContainer.setConcurrentConsumers(1);
                    listenerContainer.setPrefetchCount(1);
                    listenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
                    listenerContainer.setMessageListener(rabbitMessageListener);
                    listenerContainer.addQueueNames(getQueueNames(consumer.getQueueList()));
                    listenerContainer.start();
                } else {
                    SimpleMessageListenerContainer listenerContainer = CONSUMER_LISTENER_MAP.get(consumerName);
                    listenerContainer.start();

                    List<String> inQueueNameList = new ArrayList<>();
                    List<String> outQueueNameList = null;
                    if (Objects.nonNull(consumer.getQueueList()) && !consumer.getQueueList().isEmpty()) {
                        outQueueNameList = consumer.getQueueList().stream().map(Queue::getQueueName).collect(Collectors.toList());
                    }

                    Collections.addAll(inQueueNameList, listenerContainer.getQueueNames());

                    List<String> deleteQueueNameList = new ArrayList<>();
                    List<String> insertQueueNameList = new ArrayList<>();


                    if (Objects.nonNull(outQueueNameList)) {
                        for (String outQueueName: outQueueNameList) {
                            if (! inQueueNameList.contains(outQueueName)) {
                                insertQueueNameList.add(outQueueName);
                            }
                        }

                        for (String inQueueName: inQueueNameList) {
                            if (! outQueueNameList.contains(inQueueName)) {
                                deleteQueueNameList.add(inQueueName);
                            }
                        }
                    }

                    if (!insertQueueNameList.isEmpty()) {
                        listenerContainer.addQueueNames(insertQueueNameList.toArray(new String[0]));
                    }

                    if (!deleteQueueNameList.isEmpty()) {
                        listenerContainer.removeQueueNames(deleteQueueNameList.toArray(new String[0]));
                    }

                }



            }

            listenerAutoClose(consumerSet);
        }
    }

    protected void listenerAutoClose(Set<String> consumerSet) {
        for (Map.Entry<String, SimpleMessageListenerContainer> entry : CONSUMER_LISTENER_MAP.entrySet()) {
            if (entry.getValue().isRunning()) {
                if (Objects.isNull(consumerSet) || ! consumerSet.contains(entry.getKey())) {
                    entry.getValue().stop();
                }
            }
        }
    }

    private String[] getQueueNames(List<Queue> queueList) {
        List<String> queueNameList = new ArrayList<>();
        for (Queue queue : queueList) {
            queueNameList.add(queue.getQueueName());
        }
        return queueNameList.toArray(new String[0]);
    }


}
