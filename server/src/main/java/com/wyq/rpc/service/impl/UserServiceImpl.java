package com.wyq.rpc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyq.common.pojo.User;
import com.wyq.common.service.UserService;
import com.wyq.rpc.provider.Provider;
import org.springframework.stereotype.Service;
import com.wyq.rpc.mapper.UserMapper;

@Service
@Provider(port = 9000)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public String sayHello(String name) {
        return "hello -=======," + name;
    }
}
