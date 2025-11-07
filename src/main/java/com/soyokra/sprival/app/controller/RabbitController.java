package com.soyokra.sprival.app.controller;

import com.soyokra.sprival.app.controller.request.RabbitSendRequest;
import com.soyokra.sprival.app.service.RabbitService;
import com.soyokra.sprival.app.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * RabbitMQ测试控制器
 *
 * @author sprival
 */
@Slf4j
@RequestMapping(value = "/rabbit")
@RestController
public class RabbitController {

    @Resource
    private RabbitService rabbitService;

    /**
     * 发送消息到RabbitMQ
     *
     * @param request 发送请求
     * @return 响应结果
     */
    @PostMapping("/send")
    public ResponseUtil<Boolean> sendMessage(@Validated @RequestBody RabbitSendRequest request) {
        boolean result = rabbitService.sendMessage(request);
        if (!result) {
            return ResponseUtil.error(500, "消息发送失败");
        }
        return ResponseUtil.success(true);
    }
}
