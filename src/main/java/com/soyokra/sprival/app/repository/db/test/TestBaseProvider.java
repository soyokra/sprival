package com.soyokra.sprival.app.repository.db.test;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 性能测试数据源基础 Provider
 * 
 * @author soyokra
 * @since 2025-11-03
 */
@DS("master")
public class TestBaseProvider<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {
}

