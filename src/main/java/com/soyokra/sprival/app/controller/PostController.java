package com.soyokra.sprival.app.controller;

import com.soyokra.sprival.app.controller.request.PostCreateRequest;
import com.soyokra.sprival.app.controller.request.PostDeleteRequest;
import com.soyokra.sprival.app.controller.request.PostQueryRequest;
import com.soyokra.sprival.app.controller.request.PostStatusUpdateRequest;
import com.soyokra.sprival.app.controller.request.PostUpdateRequest;
import com.soyokra.sprival.app.controller.request.PostViewCountRequest;
import com.soyokra.sprival.app.controller.response.PostListResponse;
import com.soyokra.sprival.app.controller.response.PostResponse;
import com.soyokra.sprival.app.service.PostService;
import com.soyokra.sprival.app.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 文章控制器
 *
 * @author sprival
 */
@Slf4j
@RequestMapping(value = "/post")
@RestController
public class PostController {

    @Resource
    private PostService postService;

    /**
     * 创建文章
     *
     * @param request 创建请求
     * @return 响应结果
     */
    @PostMapping("create")
    public ResponseUtil<Integer> createPost(@Validated @RequestBody PostCreateRequest request) {
        Integer id = postService.createPost(request);
        return ResponseUtil.success(id);
    }

    /**
     * 获取文章列表（分页）
     *
     * @param request 查询请求
     * @return 响应结果
     */
    @GetMapping("get-list")
    public ResponseUtil<PostListResponse> getPostList(@Validated @RequestBody PostQueryRequest request) {
        PostListResponse response = postService.queryPostList(request);
        return ResponseUtil.success(response);
    }

    /**
     * 根据ID获取文章详情
     *
     * @param id 文章ID
     * @return 响应结果
     */
    @GetMapping("get-by-id")
    public ResponseUtil<PostResponse> getPostById(@RequestParam(value = "id") Integer id) {
        PostResponse response = postService.getPostById(id);
        if (response == null) {
            return ResponseUtil.error(404, "文章不存在");
        }
        return ResponseUtil.success(response);
    }

    /**
     * 根据slug获取文章详情
     *
     * @param slug 文章slug
     * @return 响应结果
     */
    @GetMapping("get-by-slug")
    public ResponseUtil<PostResponse> getPostBySlug(@RequestParam(value = "slug") String slug) {
        PostResponse response = postService.getPostBySlug(slug);
        if (response == null) {
            return ResponseUtil.error(404, "文章不存在");
        }
        return ResponseUtil.success(response);
    }

    /**
     * 更新文章
     *
     * @param request 更新请求
     * @return 响应结果
     */
    @PostMapping("/update")
    public ResponseUtil<Boolean> updatePost(@Validated @RequestBody PostUpdateRequest request) {
        boolean result = postService.updatePost(request);
        if (!result) {
            return ResponseUtil.error(404, "文章不存在或更新失败");
        }
        return ResponseUtil.success(true);
    }

    /**
     * 删除文章
     *
     * @param request 删除请求
     * @return 响应结果
     */
    @PostMapping("/delete")
    public ResponseUtil<Boolean> deletePost(@Validated @RequestBody PostDeleteRequest request) {
        boolean result = postService.deletePost(request);
        if (!result) {
            return ResponseUtil.error(404, "文章不存在或删除失败");
        }
        return ResponseUtil.success(true);
    }

    /**
     * 更新文章状态
     *
     * @param request 状态更新请求
     * @return 响应结果
     */
    @PostMapping("/update-status")
    public ResponseUtil<Boolean> updatePostStatus(@Validated @RequestBody PostStatusUpdateRequest request) {
        boolean result = postService.updatePostStatus(request);
        if (!result) {
            return ResponseUtil.error(404, "文章不存在或更新失败");
        }
        return ResponseUtil.success(true);
    }

    /**
     * 增加文章浏览次数
     *
     * @param request 浏览次数请求
     * @return 响应结果
     */
    @PostMapping("/view-count")
    public ResponseUtil<Boolean> incrementViewCount(@Validated @RequestBody PostViewCountRequest request) {
        boolean result = postService.incrementViewCount(request);
        if (!result) {
            return ResponseUtil.error(404, "文章不存在或更新失败");
        }
        return ResponseUtil.success(true);
    }
}
