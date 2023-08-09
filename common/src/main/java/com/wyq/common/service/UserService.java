package com.wyq.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyq.common.pojo.User;

public interface UserService extends IService<User> {

    public String sayHello(String name);
}
