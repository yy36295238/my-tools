package test.xxx.service.impl;

import kot.bootstarter.kotmybatis.service.impl.MapperManagerServiceImpl;
import org.springframework.stereotype.Service;
import test.xxx.entity.User;
import test.xxx.service.IUserService;

/**
 * @author tom
 */
@Service
public class UserServiceImpl extends MapperManagerServiceImpl<User> implements IUserService {
}
