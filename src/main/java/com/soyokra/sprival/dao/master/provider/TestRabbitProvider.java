package com.soyokra.sprival.dao.master.provider;

import com.soyokra.sprival.dao.master.model.TestRabbit;
import com.soyokra.sprival.dao.master.mapper.TestRabbitMapper;
import com.soyokra.sprival.dao.master.contract.TestRabbitContract;
import com.soyokra.sprival.dao.master.BaseTblProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhengchenping
 * @since 2025-03-25
 */
@Service
public class TestRabbitProvider extends BaseTblProvider<TestRabbitMapper, TestRabbit> implements TestRabbitContract {
    public void incrPCount(String testId) {
        TestRabbit test = getById(testId);
        Integer count = test.getPCount();
        if (count == null) {
            count = 0;
        }
        count++;
        test.setPCount(count);
        updateById(test);
    }

    public void incrCCount(String testId) {
        TestRabbit test = getById(testId);
        Integer count = test.getCCount();
        if (count == null) {
            count = 0;
        }
        count++;
        test.setCCount(count);
        updateById(test);
    }

    public List<String> getTestIdList() {
        return list().stream().map(TestRabbit::getTestId).collect(Collectors.toList());
    }
}
