package com.soyokra.sprival.app.repository.db.test.provider;

import com.soyokra.sprival.app.repository.db.test.TestBaseProvider;
import com.soyokra.sprival.app.repository.db.test.contract.TestOrderContract;
import com.soyokra.sprival.app.repository.db.test.mapper.TestOrderMapper;
import com.soyokra.sprival.app.repository.db.test.model.TestOrder;
import org.springframework.stereotype.Service;

/**
 * 性能测试-订单表 Provider 实现类
 *
 * @author soyokra
 * @since 2025-11-03
 */
@Service
public class TestOrderProvider extends TestBaseProvider<TestOrderMapper, TestOrder> implements TestOrderContract {

}

