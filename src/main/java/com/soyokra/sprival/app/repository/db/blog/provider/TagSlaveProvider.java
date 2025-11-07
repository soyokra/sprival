package com.soyokra.sprival.app.repository.db.blog.provider;

import com.soyokra.sprival.app.repository.db.blog.BlogSlaveBaseProvider;
import com.soyokra.sprival.app.repository.db.blog.contract.TagContract;
import com.soyokra.sprival.app.repository.db.blog.mapper.TagMapper;
import com.soyokra.sprival.app.repository.db.blog.model.Tag;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author soyokra
 * @since 2025-11-07
 */
@Service
public class TagSlaveProvider extends BlogSlaveBaseProvider<TagMapper, Tag> implements TagContract {

}
