package com.soyokra.sprival.support.mq;

import com.soyokra.sprival.support.mq.rabbit.RabbitProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqFactory {
    @Autowired
    RabbitProvider rabbitProvider;

    public enum Driver {
        RABBITMQ
    }

    public IAdmin admin(Driver driver) {
        switch (driver) {
            case RABBITMQ:
                return rabbitProvider;
        }
        return rabbitProvider;
    }

    public IBroker broker(Driver driver) {
        switch (driver) {
            case RABBITMQ:
                return rabbitProvider;
        }
        return rabbitProvider;
    }


    public IConsumer consumer(Driver driver) {
        switch (driver) {
            case RABBITMQ:
                return rabbitProvider;
        }
        return rabbitProvider;
    }

}
