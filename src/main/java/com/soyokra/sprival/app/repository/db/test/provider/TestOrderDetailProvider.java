package com.soyokra.sprival.app.repository.db.test.provider;

import com.soyokra.sprival.app.repository.db.test.TestBaseProvider;
import com.soyokra.sprival.app.repository.db.test.contract.TestOrderDetailContract;
import com.soyokra.sprival.app.repository.db.test.mapper.TestOrderDetailMapper;
import com.soyokra.sprival.app.repository.db.test.model.TestOrderDetail;
import org.springframework.stereotype.Service;

/**
 * 性能测试-订单明细表 Provider 实现类
 *
 * @author soyokra
 * @since 2025-11-03
 */
@Service
public class TestOrderDetailProvider extends TestBaseProvider<TestOrderDetailMapper, TestOrderDetail> implements TestOrderDetailContract {

}

