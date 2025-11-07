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
 * @since 2025-11-07
 */
@Getter
@Setter
@TableName("post_tag")
public class PostTag implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer postId;

    private Integer tagId;
}
