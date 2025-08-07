package com.soyokra.sprival.dao.ck;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@DS("ck")
public class BaseTblProvider<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

}
