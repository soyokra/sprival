package com.soyokra.sprival.app.controller.request;

import lombok.Data;

/**
 * 文章查询请求DTO
 *
 * @author sprival
 */
@Data
public class PostQueryRequest {

    /**
     * 当前页码，从1开始
     */
    private Integer pageNo = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 文章状态：draft/published/archived
     */
    private String status;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 作者ID
     */
    private Integer authorId;

    /**
     * 关键词搜索（标题或内容）
     */
    private String keyword;
}

