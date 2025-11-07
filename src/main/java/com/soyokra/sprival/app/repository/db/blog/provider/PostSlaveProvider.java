package com.soyokra.sprival.app.repository.db.blog.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soyokra.sprival.app.repository.db.blog.BlogSlaveBaseProvider;
import com.soyokra.sprival.app.repository.db.blog.contract.PostContract;
import com.soyokra.sprival.app.repository.db.blog.mapper.PostMapper;
import com.soyokra.sprival.app.repository.db.blog.model.Post;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author soyokra
 * @since 2025-11-07
 */
@Service
public class PostSlaveProvider extends BlogSlaveBaseProvider<PostMapper, Post> implements PostContract {
    public List<Post> selectPostListByCategoryId(Integer categoryId) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Post::getCategoryId, categoryId);
        return list(queryWrapper);
    }

    /**
     * 分页查询文章列表
     *
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @param status 状态
     * @param categoryId 分类ID
     * @param authorId 作者ID
     * @param keyword 关键词
     * @return 分页结果
     */
    public IPage<Post> selectPostPage(Integer pageNo, Integer pageSize, String status,
            Integer categoryId, Integer authorId, String keyword) {
        Page<Post> page = new Page<>(pageNo, pageSize);
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(status)) {
            queryWrapper.lambda().eq(Post::getStatus, status);
        }
        if (categoryId != null) {
            queryWrapper.lambda().eq(Post::getCategoryId, categoryId);
        }
        if (authorId != null) {
            queryWrapper.lambda().eq(Post::getAuthorId, authorId);
        }
        if (StringUtils.hasText(keyword)) {
            queryWrapper.lambda().and(wrapper -> wrapper
                    .like(Post::getTitle, keyword)
                    .or()
                    .like(Post::getContent, keyword));
        }
        queryWrapper.lambda().orderByDesc(Post::getCreatedAt);
        return page(page, queryWrapper);
    }

    /**
     * 根据ID查询文章
     *
     * @param id 文章ID
     * @return 文章
     */
    public Post selectPostById(Integer id) {
        return getById(id);
    }

    /**
     * 根据slug查询文章
     *
     * @param slug 文章slug
     * @return 文章
     */
    public Post selectPostBySlug(String slug) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Post::getSlug, slug);
        return getOne(queryWrapper);
    }
}
