package com.soyokra.sprival.app.controller.request;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 增加文章浏览次数请求DTO
 *
 * @author sprival
 */
@Data
public class PostViewCountRequest {

    /**
     * 文章ID
     */
    @NotNull(message = "文章ID不能为空")
    private Integer id;
}

