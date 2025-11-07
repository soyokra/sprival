package com.soyokra.sprival.app.controller.response;

import lombok.Data;
import java.time.LocalDate;

/**
 * 文章响应DTO
 *
 * @author sprival
 */
@Data
public class PostResponse {

    /**
     * 文章ID
     */
    private Integer id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章唯一标识
     */
    private String slug;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章摘要
     */
    private String excerpt;

    /**
     * 文章状态：draft/published/archived
     */
    private String status;

    /**
     * 作者ID
     */
    private Integer authorId;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 创建时间
     */
    private LocalDate createdAt;

    /**
     * 更新时间
     */
    private LocalDate updatedAt;
}

