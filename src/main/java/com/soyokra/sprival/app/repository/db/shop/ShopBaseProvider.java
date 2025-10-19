package com.soyokra.sprival.app.repository.db.shop;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@DS("master")
public class ShopBaseProvider<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {
}