package com.soyokra.sprival.app.controller.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 创建文章请求DTO
 *
 * @author sprival
 */
@Data
public class MySqlPostCreateRequest {

    /**
     * 文章标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    /**
     * 文章唯一标识
     */
    @NotBlank(message = "slug不能为空")
    @Size(max = 200, message = "slug长度不能超过200个字符")
    private String slug;

    /**
     * 文章内容
     */
    @NotBlank(message = "内容不能为空")
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
     * 作者ID
     */
    @NotNull(message = "作者ID不能为空")
    private Integer authorId;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Integer categoryId;
}

