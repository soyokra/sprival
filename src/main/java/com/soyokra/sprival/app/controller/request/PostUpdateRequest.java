package com.soyokra.sprival.app.controller.request;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 更新文章请求DTO
 *
 * @author sprival
 */
@Data
public class PostUpdateRequest {

    /**
     * 文章ID
     */
    @NotNull(message = "文章ID不能为空")
    private Integer id;

    /**
     * 文章标题
     */
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    /**
     * 文章唯一标识
     */
    @Size(max = 200, message = "slug长度不能超过200个字符")
    private String slug;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章摘要
     */
    @Size(max = 500, message = "摘要长度不能超过500个字符")
    private String excerpt;

    /**
     * 文章状态：draft/published/archived
     */
    private String status;

    /**
     * 分类ID
     */
    private Integer categoryId;
}

