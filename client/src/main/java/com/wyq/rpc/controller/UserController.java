package com.wyq.rpc.controller;

import com.wyq.common.service.UserService;
import com.wyq.rpc.rpc.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    @Consumer(port = 9000,host = "127.0.0.1")
    private UserService userService;

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public void tt(String name){
        System.out.println(userService.sayHello(name));
    }

    @RequestMapping(value = "/hh",method = RequestMethod.GET)
    public Map<String, String> t1(){
        Map<String,String> map = new HashMap<>();
        map.put("1","2");
        return map;
    }
}
