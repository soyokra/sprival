package com.soyokra.sprival.controller;

import com.soyokra.sprival.dao.ck.provider.TestProvider;
import com.soyokra.sprival.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping(value = "/clickhouse")
@RestController
public class ClickHouseController {

    @Autowired
    TestProvider testProvider;

    @PostMapping(value = "/query")
    @ResponseBody
    public ResponseUtils<?> query(){
        return ResponseUtils.success(testProvider.selectByOrderId("123"));
    }
}
