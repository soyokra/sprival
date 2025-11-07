package com.soyokra.sprival.app.controller.request;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 删除文章请求DTO
 *
 * @author sprival
 */
@Data
public class PostDeleteRequest {

    /**
     * 文章ID
     */
    @NotNull(message = "文章ID不能为空")
    private Integer id;
}

