package com.soyokra.sprival.app.repository.db.blog.provider;

import com.soyokra.sprival.app.repository.db.blog.BlogSlaveBaseProvider;
import com.soyokra.sprival.app.repository.db.blog.contract.CommentContract;
import com.soyokra.sprival.app.repository.db.blog.mapper.CommentMapper;
import com.soyokra.sprival.app.repository.db.blog.model.Comment;
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
public class CommentSlaveProvider extends BlogSlaveBaseProvider<CommentMapper, Comment> implements CommentContract {

}
