package com.soyokra.sprival.app.http.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soyokra.sprival.app.http.request.TestOrderBatchInsertRequest;
import com.soyokra.sprival.app.http.request.TestOrderInsertRequest;
import com.soyokra.sprival.app.http.request.TestOrderQueryRequest;
import com.soyokra.sprival.app.http.request.TestOrderUpdateRequest;
import com.soyokra.sprival.app.http.response.TestOrderStatisticsResponse;
import com.soyokra.sprival.app.http.response.TestOrderWithDetailResponse;
import com.soyokra.sprival.app.repository.db.test.model.TestOrder;
import com.soyokra.sprival.app.service.TestOrderService;
import com.soyokra.sprival.app.util.ResponseUtils;
import com.soyokra.sprival.app.util.TestDataGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 性能测试-订单控制器
 * 
 * 提供各种操作接口用于性能测试
 *
 * @author soyokra
 * @since 2025-11-03
 */
@Slf4j
@RestController
@RequestMapping(value = "/test/order")
public class TestOrderController {

    @Resource
    private TestOrderService testOrderService;
    
    private static final Random RANDOM = new Random();

    /**
     * 单条插入订单
     * 
     * 用于测试单条记录插入性能
     *
     * @param request 订单信息
     * @return 通用响应
     */
    @PostMapping("/insert")
    public ResponseUtils<?> insert(@RequestBody @Validated TestOrderInsertRequest request) {
        log.debug("插入订单: orderNo={}", request.getOrderNo());
        
        TestOrder order = new TestOrder();
        BeanUtils.copyProperties(request, order);
        
        boolean success = testOrderService.insertOrder(order);
        
        if (success) {
            return ResponseUtils.success(order.getOrderId());
        } else {
            return ResponseUtils.error(500, "插入失败");
        }
    }

    /**
     * 批量插入订单（用于数据预填充）
     * 
     * 用于快速填充大量测试数据
     *
     * @param request 批量插入请求
     * @return 通用响应
     */
    @PostMapping("/batchInsert")
    public ResponseUtils<?> batchInsert(@RequestBody @Validated TestOrderBatchInsertRequest request) {
        log.info("批量插入订单: batchSize={}, batchCount={}", request.getBatchSize(), request.getBatchCount());
        
        int totalInserted = 0;
        long startUserId = request.getStartUserId() != null ? request.getStartUserId() : 1L;
        
        for (int i = 0; i < request.getBatchCount(); i++) {
            List<TestOrder> orders = new ArrayList<>();
            
            for (int j = 0; j < request.getBatchSize(); j++) {
                TestOrder order = TestDataGenerator.generateTestOrder(startUserId + (i * request.getBatchSize() + j) % 10000);
                orders.add(order);
            }
            
            boolean success = testOrderService.batchInsertOrders(orders);
            if (success) {
                totalInserted += orders.size();
            }
            
            log.debug("第 {} 批插入完成，当前总数: {}", i + 1, totalInserted);
        }
        
        log.info("批量插入完成，总计: {} 条记录", totalInserted);
        
        return ResponseUtils.success(totalInserted);
    }

    /**
     * 根据订单ID查询订单（主键查询）
     * 
     * 用于测试主键索引查询性能
     *
     * @param orderId 订单ID
     * @return 订单信息
     */
    @GetMapping("/get/{orderId}")
    public ResponseUtils<?> getById(@PathVariable Long orderId) {
        log.debug("查询订单: orderId={}", orderId);
        
        TestOrder order = testOrderService.getByOrderId(orderId);
        
        if (order != null) {
            return ResponseUtils.success(order);
        } else {
            return ResponseUtils.error(404, "订单不存在");
        }
    }

    /**
     * 根据订单号查询订单（唯一索引查询）
     * 
     * 用于测试唯一索引查询性能
     *
     * @param orderNo 订单号
     * @return 订单信息
     */
    @GetMapping("/getByOrderNo")
    public ResponseUtils<?> getByOrderNo(@RequestParam String orderNo) {
        log.debug("查询订单: orderNo={}", orderNo);
        
        TestOrder order = testOrderService.getByOrderNo(orderNo);
        
        if (order != null) {
            return ResponseUtils.success(order);
        } else {
            return ResponseUtils.error(404, "订单不存在");
        }
    }

    /**
     * 条件查询订单列表
     * 
     * 用于测试索引覆盖查询性能
     *
     * @param request 查询条件
     * @return 订单列表
     */
    @GetMapping("/query")
    public ResponseUtils<?> query(TestOrderQueryRequest request) {
        log.debug("查询订单列表: userId={}, status={}", request.getUserId(), request.getStatus());
        
        List<TestOrder> orders = testOrderService.queryOrders(request);
        return ResponseUtils.success(orders);
    }

    /**
     * 分页查询订单列表
     * 
     * 用于测试分页查询性能
     *
     * @param request 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    public ResponseUtils<?> page(TestOrderQueryRequest request) {
        log.debug("分页查询订单: pageNo={}, pageSize={}", request.getPageNo(), request.getPageSize());
        
        IPage<TestOrder> page = testOrderService.queryOrdersPage(request);
        return ResponseUtils.success(page);
    }

    /**
     * 更新订单
     * 
     * 用于测试更新操作性能
     *
     * @param request 更新请求
     * @return 通用响应
     */
    @PutMapping("/update")
    public ResponseUtils<?> update(@RequestBody @Validated TestOrderUpdateRequest request) {
        log.debug("更新订单: orderId={}", request.getOrderId());
        
        TestOrder order = new TestOrder();
        BeanUtils.copyProperties(request, order);
        
        boolean success = testOrderService.updateOrder(order);
        
        if (success) {
            return ResponseUtils.success();
        } else {
            return ResponseUtils.error(500, "更新失败");
        }
    }

    /**
     * 删除订单
     * 
     * 用于测试删除操作性能
     *
     * @param orderId 订单ID
     * @return 通用响应
     */
    @DeleteMapping("/delete/{orderId}")
    public ResponseUtils<?> delete(@PathVariable Long orderId) {
        log.debug("删除订单: orderId={}", orderId);
        
        boolean success = testOrderService.deleteOrder(orderId);
        
        if (success) {
            return ResponseUtils.success();
        } else {
            return ResponseUtils.error(500, "删除失败");
        }
    }

    /**
     * 订单统计
     * 
     * 用于测试聚合查询性能
     *
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ResponseUtils<?> statistics() {
        log.debug("查询订单统计");
        
        TestOrderStatisticsResponse statistics = testOrderService.getStatistics();
        return ResponseUtils.success(statistics);
    }

    /**
     * 查询订单及明细（测试 JOIN 查询性能）
     *
     * @param orderId 订单ID
     * @return 订单及明细信息
     */
    @GetMapping("/withDetail/{orderId}")
    public ResponseUtils<?> getOrderWithDetails(@PathVariable Long orderId) {
        log.debug("查询订单及明细: orderId={}", orderId);
        
        TestOrderWithDetailResponse response = testOrderService.getOrderWithDetails(orderId);
        
        if (response != null) {
            return ResponseUtils.success(response);
        } else {
            return ResponseUtils.error(404, "订单不存在");
        }
    }

    /**
     * 插入订单及明细（测试事务性能和 JOIN 查询数据准备）
     *
     * @param request 订单信息
     * @return 通用响应
     */
    @PostMapping("/insertWithDetails")
    public ResponseUtils<?> insertWithDetails(@RequestBody @Validated TestOrderInsertRequest request) {
        log.debug("插入订单及明细: orderNo={}", request.getOrderNo());
        
        TestOrder order = new TestOrder();
        BeanUtils.copyProperties(request, order);
        
        // 随机生成 1-5 个明细
        int detailCount = RANDOM.nextInt(5) + 1;
        boolean success = testOrderService.insertOrderWithDetails(order, detailCount);
        
        if (success) {
            return ResponseUtils.success(order.getOrderId());
        } else {
            return ResponseUtils.error(500, "插入失败");
        }
    }

    /**
     * 混合操作（70%读 + 30%写）
     * 
     * 用于模拟真实业务场景
     *
     * @return 通用响应
     */
    @PostMapping("/mixedOperation")
    public ResponseUtils<?> mixedOperation() {
        // 随机决定执行读或写操作
        double random = Math.random();
        
        if (random < 0.7) {
            // 70% 读操作
            Long randomUserId = (long) (Math.random() * 10000 + 1);
            TestOrderQueryRequest request = new TestOrderQueryRequest();
            request.setUserId(randomUserId);
            request.setPageNo(1);
            request.setPageSize(10);
            
            IPage<TestOrder> page = testOrderService.queryOrdersPage(request);
            return ResponseUtils.success(page);
        } else {
            // 30% 写操作
            TestOrder order = TestDataGenerator.generateTestOrder((long) (Math.random() * 10000 + 1));
            boolean success = testOrderService.insertOrder(order);
            
            if (success) {
                return ResponseUtils.success(order.getOrderId());
            } else {
                return ResponseUtils.error(500, "插入失败");
            }
        }
    }
}

