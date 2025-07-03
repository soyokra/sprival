package com.soyokra.sprival.controller;

import com.soyokra.sprival.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping(value = "/mongo")
@RestController
public class MongoController {
    @Autowired
    MongoTemplate mongoTemplate;

    @PostMapping(value = "/query")
    @ResponseBody
    public ResponseUtils<?> query(){
        return ResponseUtils.success();
    }
}
