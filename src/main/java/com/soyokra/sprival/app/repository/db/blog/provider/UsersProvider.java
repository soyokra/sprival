package com.soyokra.sprival.app.repository.db.blog.provider;

import com.soyokra.sprival.app.repository.db.blog.model.Users;
import com.soyokra.sprival.app.repository.db.blog.mapper.UsersMapper;
import com.soyokra.sprival.app.repository.db.blog.contract.UsersContract;
import com.soyokra.sprival.app.repository.db.blog.BlogBaseProvider;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author soyokra
 * @since 2025-11-06
 */
@Service
public class UsersProvider extends BlogBaseProvider<UsersMapper, Users> implements UsersContract {

}
