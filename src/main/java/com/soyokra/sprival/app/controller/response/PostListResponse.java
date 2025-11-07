package com.soyokra.sprival.app.controller.response;

import lombok.Data;
import java.util.List;

/**
 * 文章列表响应DTO（分页）
 *
 * @author sprival
 */
@Data
public class PostListResponse {

    /**
     * 文章列表
     */
    private List<PostResponse> records;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 总页数
     */
    private Long pages;
}

