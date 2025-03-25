package com.soyokra.sprival.controller;

import com.soyokra.sprival.controller.request.rabbit.DeclareQueueRequest;
import com.soyokra.sprival.controller.request.rabbit.PublishRequest;
import com.soyokra.sprival.controller.request.rabbit.TestRequest;
import com.soyokra.sprival.dao.master.provider.TestRabbitProvider;
import com.soyokra.sprival.support.mq.MqFactory;
import com.soyokra.sprival.support.mq.IAdmin;
import com.soyokra.sprival.support.mq.IBroker;
import com.soyokra.sprival.support.mq.Queue;
import com.soyokra.sprival.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping(value = "/rabbit")
@RestController
public class RabbitController {

    @Autowired
    MqFactory mqFactory;

    @Autowired
    TestRabbitProvider testRabbitProvider;

    @PostMapping(value = "/declareQueue")
    @ResponseBody
    public ResponseUtils<?> declareQueue(@RequestBody DeclareQueueRequest request){
        IAdmin admin = mqFactory.admin(MqFactory.Driver.RABBITMQ);
        Queue queue = new Queue();
        queue.setQueueName(request.getQueueName());
        queue.setDelayTime(request.getDelayTime());
        admin.declareQueue(queue);
        return ResponseUtils.success();
    }

    @PostMapping(value = "/publish")
    @ResponseBody
    public ResponseUtils<?> publish(@RequestBody PublishRequest request){
        IBroker broker = mqFactory.broker(MqFactory.Driver.RABBITMQ);
        Queue queue = new Queue();
        queue.setQueueName(request.getQueueName());
        queue.setDelayTime(request.getDelayTime());
        broker.publish(queue, request.getContent());
        return ResponseUtils.success();
    }

    @PostMapping(value = "/test")
    @ResponseBody
    public ResponseUtils<?> test(@RequestBody TestRequest request){
        IBroker broker = mqFactory.broker(MqFactory.Driver.RABBITMQ);
        Queue queue = new Queue();
        queue.setQueueName(request.getQueueName());
        queue.setDelayTime(request.getDelayTime());

        List<String> testIdList = testRabbitProvider.getTestIdList();
        for (String testId : testIdList) {
            testRabbitProvider.incrPCount(testId);
            broker.publish(queue, testId);
        }

        return ResponseUtils.success();
    }

}
