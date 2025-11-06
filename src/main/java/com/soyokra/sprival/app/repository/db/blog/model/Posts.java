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
 * @since 2025-11-06
 */
@Getter
@Setter
public class Posts implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String title;

    private String slug;

    private String content;

    private String excerpt;

    private String status;

    private Integer authorId;

    private Integer categoryId;

    private Integer viewCount;

    private LocalDate createdAt;

    private LocalDate updatedAt;
}
