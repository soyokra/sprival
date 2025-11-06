package com.soyokra.sprival.app.repository.db.blog.provider;

import com.soyokra.sprival.app.repository.db.blog.model.Posts;
import com.soyokra.sprival.app.repository.db.blog.mapper.PostsMapper;
import com.soyokra.sprival.app.repository.db.blog.contract.PostsContract;
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
public class PostsProvider extends BlogBaseProvider<PostsMapper, Posts> implements PostsContract {

}
