package com.soyokra.sprival.app.repository.db.blog.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
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
@TableName("post_tags")
public class PostTags implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("post_id")
    private Integer postId;

    @TableId("tag_id")
    private Integer tagId;
}
