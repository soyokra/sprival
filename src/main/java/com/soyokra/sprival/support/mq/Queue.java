package com.soyokra.sprival.support.mq;

import lombok.Data;
import java.io.Serializable;

@Data
public class Queue implements Serializable {
    private String queueName;

    private Integer delayTime;

    public static Queue valueOf(final String queueName) {
        Queue queue = new Queue();
        queue.setQueueName(queueName);
        return queue;
    }

    public static Queue valueOf(final String queueName, final int delayTime) {
        Queue queue = new Queue();
        queue.setQueueName(queueName);
        queue.setDelayTime(delayTime);
        return queue;
    }
}
