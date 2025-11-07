package com.soyokra.sprival.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soyokra.sprival.app.controller.request.PostCreateRequest;
import com.soyokra.sprival.app.controller.request.PostDeleteRequest;
import com.soyokra.sprival.app.controller.request.PostQueryRequest;
import com.soyokra.sprival.app.controller.request.PostStatusUpdateRequest;
import com.soyokra.sprival.app.controller.request.PostUpdateRequest;
import com.soyokra.sprival.app.controller.request.PostViewCountRequest;
import com.soyokra.sprival.app.controller.response.PostListResponse;
import com.soyokra.sprival.app.controller.response.PostResponse;
import com.soyokra.sprival.app.repository.db.blog.model.Post;
import com.soyokra.sprival.app.repository.db.blog.provider.PostProvider;
import com.soyokra.sprival.app.repository.db.blog.provider.PostSlaveProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章服务类
 *
 * @author sprival
 */
@Slf4j
@Service
public class PostService {

    @Resource
    private PostProvider postProvider;

    @Resource
    private PostSlaveProvider postSlaveProvider;

    /**
     * 创建文章
     *
     * @param request 创建请求
     * @return 文章ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer createPost(PostCreateRequest request) {
        log.info("创建文章，title: {}", request.getTitle());
        Post post = new Post();
        BeanUtils.copyProperties(request, post);
        if (post.getStatus() == null || post.getStatus().isEmpty()) {
            post.setStatus("draft");
        }
        post.setViewCount(0);
        post.setCreatedAt(LocalDate.now());
        post.setUpdatedAt(LocalDate.now());
        postProvider.save(post);
        log.info("创建文章成功，id: {}", post.getId());
        return post.getId();
    }

    /**
     * 分页查询文章列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    public PostListResponse queryPostList(PostQueryRequest request) {
        log.info("分页查询文章列表，pageNo: {}, pageSize: {}", request.getPageNo(), request.getPageSize());
        IPage<Post> page = postSlaveProvider.selectPostPage(
                request.getPageNo(),
                request.getPageSize(),
                request.getStatus(),
                request.getCategoryId(),
                request.getAuthorId(),
                request.getKeyword()
        );

        PostListResponse response = new PostListResponse();
        List<PostResponse> records = page.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        response.setRecords(records);
        response.setTotal(page.getTotal());
        response.setCurrent(page.getCurrent());
        response.setSize(page.getSize());
        response.setPages(page.getPages());
        return response;
    }

    /**
     * 根据ID获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    public PostResponse getPostById(Integer id) {
        log.info("根据ID获取文章详情，id: {}", id);
        Post post = postSlaveProvider.selectPostById(id);
        if (post == null) {
            return null;
        }
        // 增加浏览次数（通过代理对象调用，确保事务生效）
        ((PostService) AopContext.currentProxy()).incrementViewCount(id);
        return convertToResponse(post);
    }

    /**
     * 根据slug获取文章详情
     *
     * @param slug 文章slug
     * @return 文章详情
     */
    public PostResponse getPostBySlug(String slug) {
        log.info("根据slug获取文章详情，slug: {}", slug);
        Post post = postSlaveProvider.selectPostBySlug(slug);
        if (post == null) {
            return null;
        }
        // 增加浏览次数（通过代理对象调用，确保事务生效）
        ((PostService) AopContext.currentProxy()).incrementViewCount(post.getId());
        return convertToResponse(post);
    }

    /**
     * 更新文章
     *
     * @param request 更新请求
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePost(PostUpdateRequest request) {
        log.info("更新文章，id: {}", request.getId());
        Post post = postSlaveProvider.selectPostById(request.getId());
        if (post == null) {
            log.warn("文章不存在，id: {}", request.getId());
            return false;
        }
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getSlug() != null) {
            post.setSlug(request.getSlug());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getExcerpt() != null) {
            post.setExcerpt(request.getExcerpt());
        }
        if (request.getStatus() != null) {
            post.setStatus(request.getStatus());
        }
        if (request.getCategoryId() != null) {
            post.setCategoryId(request.getCategoryId());
        }
        post.setUpdatedAt(LocalDate.now());
        boolean result = postProvider.updateById(post);
        log.info("更新文章{}，id: {}", result ? "成功" : "失败", request.getId());
        return result;
    }

    /**
     * 删除文章
     *
     * @param request 删除请求
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePost(PostDeleteRequest request) {
        log.info("删除文章，id: {}", request.getId());
        boolean result = postProvider.removeById(request.getId());
        log.info("删除文章{}，id: {}", result ? "成功" : "失败", request.getId());
        return result;
    }

    /**
     * 更新文章状态
     *
     * @param request 状态更新请求
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePostStatus(PostStatusUpdateRequest request) {
        log.info("更新文章状态，id: {}, status: {}", request.getId(), request.getStatus());
        Post post = postSlaveProvider.selectPostById(request.getId());
        if (post == null) {
            log.warn("文章不存在，id: {}", request.getId());
            return false;
        }
        post.setStatus(request.getStatus());
        post.setUpdatedAt(LocalDate.now());
        boolean result = postProvider.updateById(post);
        log.info("更新文章状态{}，id: {}", result ? "成功" : "失败", request.getId());
        return result;
    }

    /**
     * 增加文章浏览次数
     *
     * @param request 浏览次数请求
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementViewCount(PostViewCountRequest request) {
        return incrementViewCount(request.getId());
    }

    /**
     * 增加文章浏览次数（内部方法）
     *
     * @param id 文章ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementViewCount(Integer id) {
        Post post = postSlaveProvider.selectPostById(id);
        if (post == null) {
            return false;
        }
        post.setViewCount(post.getViewCount() == null ? 1 : post.getViewCount() + 1);
        return postProvider.updateById(post);
    }

    /**
     * 转换为响应DTO
     *
     * @param post 文章实体
     * @return 响应DTO
     */
    private PostResponse convertToResponse(Post post) {
        PostResponse response = new PostResponse();
        BeanUtils.copyProperties(post, response);
        return response;
    }
}
