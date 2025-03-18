package com.soyokra.sprival.support.mq.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RabbitHelper {

    public String buildDelayName(String name) {
        return name + "_delay";
    }

    public FanoutExchange buildExchange(String name) {
        return new FanoutExchange(name);
    }

    public Queue buildQueue(String name) {
        return new Queue(name);
    }

    public Binding buildBinding(Queue queue, FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

    public Queue buildDelayQueue(String name, String delayName, Integer delayTime) {
        Map<String, Object> args = new HashMap<>(3);
        // x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", name);
        // x-dead-letter-routing-key  这里声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", "");
        // x-message-ttl  声明队列的TTL  单位是毫秒
        args.put("x-message-ttl", 1000 * delayTime);

        return QueueBuilder.durable(delayName).withArguments(args).build();
    }
}
