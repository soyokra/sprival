package com.soyokra.sprival.controller;

import com.soyokra.sprival.support.ck.ClickHouseTemplate;
import com.soyokra.sprival.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping(value = "/clickhouse")
@RestController
public class ClickHouseController {

    @Autowired
    ClickHouseTemplate clickHouseTemplate;

    @PostMapping(value = "/query")
    @ResponseBody
    public ResponseUtils<?> query(){
        clickHouseTemplate.query("test");
        return ResponseUtils.success();
    }
}
