package com.soyokra.sprival.support.mq;

import java.util.List;

public interface IConsumer {
    void listenerUp(List<Consumer> consumerList);

    void listenerDown(List<Consumer> consumerList);
}
