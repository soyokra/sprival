package com.soyokra.sprival.app.controller.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 更新文章状态请求DTO
 *
 * @author sprival
 */
@Data
public class PostStatusUpdateRequest {

    /**
     * 文章ID
     */
    @NotNull(message = "文章ID不能为空")
    private Integer id;

    /**
     * 文章状态：draft/published/archived
     */
    @NotBlank(message = "状态不能为空")
    private String status;
}

