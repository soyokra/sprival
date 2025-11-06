package com.soyokra.sprival.app.repository.db.blog.provider;

import com.soyokra.sprival.app.repository.db.blog.model.Comments;
import com.soyokra.sprival.app.repository.db.blog.mapper.CommentsMapper;
import com.soyokra.sprival.app.repository.db.blog.contract.CommentsContract;
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
public class CommentsProvider extends BlogBaseProvider<CommentsMapper, Comments> implements CommentsContract {

}
