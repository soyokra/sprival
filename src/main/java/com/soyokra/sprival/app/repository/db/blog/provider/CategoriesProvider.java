package com.soyokra.sprival.app.repository.db.blog.provider;

import com.soyokra.sprival.app.repository.db.blog.model.Categories;
import com.soyokra.sprival.app.repository.db.blog.mapper.CategoriesMapper;
import com.soyokra.sprival.app.repository.db.blog.contract.CategoriesContract;
import com.soyokra.sprival.app.repository.db.blog.BlogBaseProvider;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author soyokra
 * @since 2025-11-06
 */
@Service
public class CategoriesProvider extends BlogBaseProvider<CategoriesMapper, Categories> implements CategoriesContract {

}
