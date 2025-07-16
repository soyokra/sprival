package com.soyokra.sprival.support.mq;

import com.soyokra.sprival.support.mq.kafka.KafkaProvider;
import com.soyokra.sprival.support.mq.rabbit.RabbitProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqFactory {
    @Autowired
    RabbitProvider rabbitProvider;

    @Autowired
    KafkaProvider kafkaProvider;

    public enum Driver {
        RABBITMQ,
        KAFKA
    }

    public IAdmin admin(Driver driver) {
        switch (driver) {
            case RABBITMQ:
                return rabbitProvider;
            case KAFKA:
                return kafkaProvider;
        }
        return rabbitProvider;
    }

    public IBroker broker(Driver driver) {
        switch (driver) {
            case RABBITMQ:
                return rabbitProvider;
            case KAFKA:
                return kafkaProvider;
        }
        return rabbitProvider;
    }


    public IConsumer consumer(Driver driver) {
        switch (driver) {
            case RABBITMQ:
                return rabbitProvider;
            case KAFKA:
                return kafkaProvider;
        }
        return rabbitProvider;
    }

}
