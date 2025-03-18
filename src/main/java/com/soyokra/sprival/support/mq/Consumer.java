package com.soyokra.sprival.support.mq;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class Consumer implements Serializable {
    private String consumerName;

    private List<Queue> queueList;


    public static Consumer valueOf(String consumerName, List<Queue> queueList) {
        Consumer consumer = new Consumer();
        consumer.setConsumerName(consumerName);
        consumer.setQueueList(queueList);
        return consumer;
    }

    public static Consumer valueOf(String consumerName, Queue queue) {
        return valueOf(consumerName, Collections.singletonList(queue));
    }
}
