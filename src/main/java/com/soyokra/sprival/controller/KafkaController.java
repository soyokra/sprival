package com.soyokra.sprival.controller;

import com.soyokra.sprival.controller.request.kafka.PublishRequest;
import com.soyokra.sprival.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping(value = "/kafka")
@RestController
public class KafkaController {
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping(value = "/publish")
    @ResponseBody
    public ResponseUtils<?> publish(@RequestBody PublishRequest request){
        kafkaTemplate.send(request.getTopic(), request.getContent());
        return ResponseUtils.success();
    }
}
