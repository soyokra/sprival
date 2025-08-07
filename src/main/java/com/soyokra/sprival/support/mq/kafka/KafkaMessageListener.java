package com.soyokra.sprival.support.mq.kafka;

public class KafkaMessageListener {
    public void handleMessage(Object message) {
        System.out.println("收到消息: " + message);
    }
}
