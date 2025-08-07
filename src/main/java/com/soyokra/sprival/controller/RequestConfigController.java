package com.soyokra.sprival.controller;


import com.google.common.util.concurrent.RateLimiter;
import com.soyokra.sprival.controller.request.rabbit.DeclareQueueRequest;
import com.soyokra.sprival.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping(value = "/request-config")
@RestController
public class RequestConfigController {

    @PostMapping(value = "/test")
    @ResponseBody
    public ResponseUtils<?> test(@RequestBody DeclareQueueRequest request){
        // 分布式锁

        // 限流

        // 日志：url，请求头，请求方法，请求体，返回头，返回状态码，返回体

       return ResponseUtils.success();
    }
}
