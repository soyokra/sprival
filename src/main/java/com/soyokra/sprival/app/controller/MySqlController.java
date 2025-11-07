package com.soyokra.sprival.app.controller;

import com.soyokra.sprival.app.controller.request.MySqlPostCreateRequest;
import com.soyokra.sprival.app.controller.request.MySqlPostQueryRequest;
import com.soyokra.sprival.app.controller.response.PostListResponse;
import com.soyokra.sprival.app.service.MySqlService;
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
@RequestMapping(value = "/mysql")
@RestController
public class MySqlController {

    @Resource
    private MySqlService mySqlService;

    /**
     * 创建文章
     *
     * @param request 创建请求
     * @return 响应结果
     */
    @PostMapping("create")
    public ResponseUtil<Integer> createPost(@Validated @RequestBody MySqlPostCreateRequest request) {
        Integer id = mySqlService.createPost(request);
        return ResponseUtil.success(id);
    }

    /**
     * 获取文章列表（分页）
     *
     * @param request 查询请求
     * @return 响应结果
     */
    @GetMapping("get-list")
    public ResponseUtil<PostListResponse> getPostList(@Validated @RequestBody MySqlPostQueryRequest request) {
        PostListResponse response = mySqlService.queryPostList(request);
        return ResponseUtil.success(response);
    }

}
