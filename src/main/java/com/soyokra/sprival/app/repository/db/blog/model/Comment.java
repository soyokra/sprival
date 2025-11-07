package com.soyokra.sprival.app.repository.db.blog.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author soyokra
 * @since 2025-11-07
 */
@Getter
@Setter
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer postId;

    private Integer parentId;

    private Integer authorId;

    private String content;

    private String status;

    private LocalDate createdAt;
}
