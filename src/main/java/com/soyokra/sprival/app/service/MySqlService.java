package com.soyokra.sprival.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soyokra.sprival.app.controller.request.MySqlPostCreateRequest;
import com.soyokra.sprival.app.controller.request.MySqlPostQueryRequest;
import com.soyokra.sprival.app.controller.response.PostListResponse;
import com.soyokra.sprival.app.controller.response.PostResponse;
import com.soyokra.sprival.app.repository.db.blog.model.Post;
import com.soyokra.sprival.app.repository.db.blog.provider.PostProvider;
import com.soyokra.sprival.app.repository.db.blog.provider.PostSlaveProvider;
import lombok.extern.slf4j.Slf4j;
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
public class MySqlService {

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
    public Integer createPost(MySqlPostCreateRequest request) {
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
    public PostListResponse queryPostList(MySqlPostQueryRequest request) {
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
