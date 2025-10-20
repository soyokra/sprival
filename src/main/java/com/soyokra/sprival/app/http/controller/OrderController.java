package com.soyokra.sprival.app.http.controller;

import com.soyokra.sprival.app.http.request.OrderInsertRequest;
import com.soyokra.sprival.app.repository.db.shop.model.OrderTbl;
import com.soyokra.sprival.app.repository.db.shop.provider.OrderTblProvider;
import com.soyokra.sprival.app.service.OrderService;
import com.soyokra.sprival.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Slf4j
@RequestMapping(value = "/order")
@RestController
public class OrderController {
    @Resource
    private OrderTblProvider orderProvider;

    @Resource
    private OrderService orderService;

    @PostMapping(value = "insert")
    public @ResponseBody
    ResponseUtils<?> insert(@RequestBody @Validated OrderInsertRequest request) throws Exception {
        OrderTbl order = new OrderTbl();
        BeanUtils.copyProperties(request, order);
        LocalDateTime now = LocalDateTime.now();
        order.setCreateTime(now);
        order.setUpdateTime(now);
        order.setStartTime(now);
        order.setEndTime(now);
        orderProvider.save(order);
        return ResponseUtils.success();
    }

    @GetMapping(value = "getCache")
    public @ResponseBody
    ResponseUtils<?> getCache(@RequestParam("orderId") String orderId) throws Exception {
        return ResponseUtils.success(orderService.getOrder(orderId));
    }
}
