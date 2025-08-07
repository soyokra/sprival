package com.soyokra.sprival.dao.ck.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.soyokra.sprival.dao.ck.BaseTblProvider;
import com.soyokra.sprival.dao.ck.contract.TestContract;
import com.soyokra.sprival.dao.ck.mapper.TestMapper;
import com.soyokra.sprival.dao.ck.model.Test;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhengchenping
 * @since 2025-02-19
 */
@Service
public class TestProvider extends BaseTblProvider<TestMapper, Test> implements TestContract {
    public Test selectByOrderId(String orderId) {
        QueryWrapper<Test> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Test::getOrderId, orderId);
        return getOne(queryWrapper);
    }
}
