package com.soyokra.sprival.app.repository.db.blog.provider;

import com.soyokra.sprival.app.repository.db.blog.BlogSlaveBaseProvider;
import com.soyokra.sprival.app.repository.db.blog.contract.PostTagContract;
import com.soyokra.sprival.app.repository.db.blog.mapper.PostTagMapper;
import com.soyokra.sprival.app.repository.db.blog.model.PostTag;
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
public class PostTagSlaveProvider extends BlogSlaveBaseProvider<PostTagMapper, PostTag> implements PostTagContract {

}
