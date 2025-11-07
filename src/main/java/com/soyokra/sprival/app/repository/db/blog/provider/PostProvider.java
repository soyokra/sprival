package com.soyokra.sprival.app.repository.db.blog.provider;

import com.soyokra.sprival.app.repository.db.blog.model.Post;
import com.soyokra.sprival.app.repository.db.blog.mapper.PostMapper;
import com.soyokra.sprival.app.repository.db.blog.contract.PostContract;
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
public class PostProvider extends BlogBaseProvider<PostMapper, Post> implements PostContract {

}
