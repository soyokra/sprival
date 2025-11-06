package com.soyokra.sprival.app.repository.db.blog;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@DS("slave")
public class BlogSlaveBaseProvider<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {
}