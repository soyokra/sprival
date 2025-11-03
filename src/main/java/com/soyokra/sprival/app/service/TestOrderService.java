package com.soyokra.sprival.app.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soyokra.sprival.app.http.request.TestOrderQueryRequest;
import com.soyokra.sprival.app.http.response.TestOrderStatisticsResponse;
import com.soyokra.sprival.app.http.response.TestOrderWithDetailResponse;
import com.soyokra.sprival.app.repository.db.test.model.TestOrder;
import com.soyokra.sprival.app.repository.db.test.model.TestOrderDetail;
import com.soyokra.sprival.app.repository.db.test.provider.TestOrderProvider;
import com.soyokra.sprival.app.repository.db.test.provider.TestOrderDetailProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 性能测试-订单服务类
 *
 * @author soyokra
 * @since 2025-11-03
 */
@Slf4j
@Service
public class TestOrderService {

    @Resource
    private TestOrderProvider testOrderProvider;

    @Resource
    private TestOrderDetailProvider testOrderDetailProvider;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private static final Random RANDOM = new Random();

    /**
     * 单条插入订单
     *
     * @param order 订单信息
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean insertOrder(TestOrder order) {
        LocalDateTime now = LocalDateTime.now();
        order.setOrderTime(now);
        order.setCreateTime(now);
        order.setUpdateTime(now);
        if (order.getStatus() == null) {
            order.setStatus(0); // 默认待支付
        }
        return testOrderProvider.save(order);
    }

    /**
     * 插入订单及明细（用于测试事务和 JOIN 查询）
     *
     * @param order 订单信息
     * @param detailCount 明细数量（随机生成）
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean insertOrderWithDetails(TestOrder order, int detailCount) {
        // 插入订单
        boolean orderSaved = insertOrder(order);
        if (!orderSaved) {
            return false;
        }
        
        // 生成并插入订单明细
        List<TestOrderDetail> details = new ArrayList<>();
        for (int i = 0; i < detailCount; i++) {
            TestOrderDetail detail = new TestOrderDetail();
            detail.setOrderId(order.getOrderId());
            detail.setProductId(order.getProductId());
            detail.setProductName(order.getProductName());
            detail.setQuantity(RANDOM.nextInt(5) + 1);
            detail.setUnitPrice(order.getUnitPrice());
            detail.setSubtotal(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
            detail.setCreateTime(LocalDateTime.now());
            details.add(detail);
        }
        
        return testOrderDetailProvider.saveBatch(details);
    }

    /**
     * 批量插入订单（用于数据预填充）
     *
     * @param orders 订单列表
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batchInsertOrders(List<TestOrder> orders) {
        LocalDateTime now = LocalDateTime.now();
        for (TestOrder order : orders) {
            order.setOrderTime(now);
            order.setCreateTime(now);
            order.setUpdateTime(now);
            if (order.getStatus() == null) {
                order.setStatus(0);
            }
        }
        return testOrderProvider.saveBatch(orders);
    }

    /**
     * 根据订单ID查询订单（主键查询）
     *
     * @param orderId 订单ID
     * @return 订单信息
     */
    public TestOrder getByOrderId(Long orderId) {
        return testOrderProvider.getById(orderId);
    }

    /**
     * 根据订单号查询订单（唯一索引查询）
     *
     * @param orderNo 订单号
     * @return 订单信息
     */
    public TestOrder getByOrderNo(String orderNo) {
        QueryWrapper<TestOrder> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TestOrder::getOrderNo, orderNo);
        return testOrderProvider.getOne(wrapper);
    }

    /**
     * 条件查询订单列表
     *
     * @param request 查询条件
     * @return 订单列表
     */
    public List<TestOrder> queryOrders(TestOrderQueryRequest request) {
        QueryWrapper<TestOrder> wrapper = buildQueryWrapper(request);
        return testOrderProvider.list(wrapper);
    }

    /**
     * 分页查询订单列表
     *
     * @param request 查询条件
     * @return 分页结果
     */
    public IPage<TestOrder> queryOrdersPage(TestOrderQueryRequest request) {
        Page<TestOrder> page = new Page<>(request.getPageNo(), request.getPageSize());
        QueryWrapper<TestOrder> wrapper = buildQueryWrapper(request);
        return testOrderProvider.page(page, wrapper);
    }

    /**
     * 更新订单
     *
     * @param order 订单信息
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrder(TestOrder order) {
        order.setUpdateTime(LocalDateTime.now());
        if (order.getStatus() != null && order.getStatus() == 1 && order.getPaymentTime() == null) {
            // 状态变更为已支付时，设置支付时间
            order.setPaymentTime(LocalDateTime.now());
        }
        return testOrderProvider.updateById(order);
    }

    /**
     * 删除订单
     *
     * @param orderId 订单ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOrder(Long orderId) {
        return testOrderProvider.removeById(orderId);
    }

    /**
     * 订单统计
     *
     * @return 统计信息
     */
    public TestOrderStatisticsResponse getStatistics() {
        QueryWrapper<TestOrder> wrapper = new QueryWrapper<>();
        
        // 总订单数
        Long totalOrders = testOrderProvider.count();
        
        // 计算总金额和平均金额
        wrapper.select("IFNULL(SUM(total_amount), 0) as total_amount", 
                      "IFNULL(AVG(total_amount), 0) as avg_amount");
        TestOrder sumResult = testOrderProvider.getOne(wrapper);
        BigDecimal totalAmount = sumResult != null ? sumResult.getTotalAmount() : BigDecimal.ZERO;
        
        // 计算平均金额
        BigDecimal avgAmount = BigDecimal.ZERO;
        if (totalOrders > 0) {
            avgAmount = totalAmount.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP);
        }
        
        // 待支付订单数
        QueryWrapper<TestOrder> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.lambda().eq(TestOrder::getStatus, 0);
        Long pendingOrders = testOrderProvider.count(pendingWrapper);
        
        // 已完成订单数
        QueryWrapper<TestOrder> completedWrapper = new QueryWrapper<>();
        completedWrapper.lambda().eq(TestOrder::getStatus, 3);
        Long completedOrders = testOrderProvider.count(completedWrapper);
        
        return TestOrderStatisticsResponse.builder()
                .totalOrders(totalOrders)
                .totalAmount(totalAmount)
                .avgOrderAmount(avgAmount)
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .build();
    }

    /**
     * 查询订单及明细（测试 JOIN 查询性能）
     *
     * @param orderId 订单ID
     * @return 订单及明细信息
     */
    public TestOrderWithDetailResponse getOrderWithDetails(Long orderId) {
        // 查询订单
        TestOrder order = testOrderProvider.getById(orderId);
        if (order == null) {
            return null;
        }
        
        // 查询订单明细
        QueryWrapper<TestOrderDetail> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TestOrderDetail::getOrderId, orderId);
        List<TestOrderDetail> details = testOrderDetailProvider.list(wrapper);
        
        // 组装响应
        TestOrderWithDetailResponse response = new TestOrderWithDetailResponse();
        response.setOrder(order);
        response.setDetails(details);
        response.setDetailCount(details.size());
        
        return response;
    }

    /**
     * 构建查询条件
     *
     * @param request 查询请求
     * @return 查询包装器
     */
    private QueryWrapper<TestOrder> buildQueryWrapper(TestOrderQueryRequest request) {
        QueryWrapper<TestOrder> wrapper = new QueryWrapper<>();
        
        if (request.getUserId() != null) {
            wrapper.lambda().eq(TestOrder::getUserId, request.getUserId());
        }
        
        if (request.getProductId() != null) {
            wrapper.lambda().eq(TestOrder::getProductId, request.getProductId());
        }
        
        if (request.getStatus() != null) {
            wrapper.lambda().eq(TestOrder::getStatus, request.getStatus());
        }
        
        if (StringUtils.hasText(request.getStartTime())) {
            LocalDateTime startTime = LocalDateTime.parse(request.getStartTime(), DATE_TIME_FORMATTER);
            wrapper.lambda().ge(TestOrder::getOrderTime, startTime);
        }
        
        if (StringUtils.hasText(request.getEndTime())) {
            LocalDateTime endTime = LocalDateTime.parse(request.getEndTime(), DATE_TIME_FORMATTER);
            wrapper.lambda().le(TestOrder::getOrderTime, endTime);
        }
        
        // 默认按创建时间倒序
        wrapper.lambda().orderByDesc(TestOrder::getCreateTime);
        
        return wrapper;
    }
}

