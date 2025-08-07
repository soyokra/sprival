package com.soyokra.sprival.support.mq;

public interface IBroker {
    void publish(Queue queue, String message);
}
