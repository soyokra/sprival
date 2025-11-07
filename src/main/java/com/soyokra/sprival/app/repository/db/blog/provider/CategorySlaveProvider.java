package com.soyokra.sprival.app.repository.db.blog.provider;

import com.soyokra.sprival.app.repository.db.blog.BlogSlaveBaseProvider;
import com.soyokra.sprival.app.repository.db.blog.contract.CategoryContract;
import com.soyokra.sprival.app.repository.db.blog.mapper.CategoryMapper;
import com.soyokra.sprival.app.repository.db.blog.model.Category;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author soyokra
 * @since 2025-11-07
 */
@Service
public class CategorySlaveProvider extends BlogSlaveBaseProvider<CategoryMapper, Category> implements CategoryContract {

}
