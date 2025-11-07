package com.soyokra.sprival.app.repository.db.blog.provider;

import com.soyokra.sprival.app.repository.db.blog.BlogSlaveBaseProvider;
import com.soyokra.sprival.app.repository.db.blog.contract.UserContract;
import com.soyokra.sprival.app.repository.db.blog.mapper.UserMapper;
import com.soyokra.sprival.app.repository.db.blog.model.User;
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
public class UserSlaveProvider extends BlogSlaveBaseProvider<UserMapper, User> implements UserContract {

}
