package com.soyokra.sprival.app.repository.db.blog.provider;

import com.soyokra.sprival.app.repository.db.blog.model.Category;
import com.soyokra.sprival.app.repository.db.blog.mapper.CategoryMapper;
import com.soyokra.sprival.app.repository.db.blog.contract.CategoryContract;
import com.soyokra.sprival.app.repository.db.blog.BlogBaseProvider;
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
public class CategoryProvider extends BlogBaseProvider<CategoryMapper, Category> implements CategoryContract {

}
