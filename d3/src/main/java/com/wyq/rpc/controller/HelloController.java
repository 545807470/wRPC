package com.wyq.rpc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @RequestMapping(value = "/h",method = RequestMethod.GET)
    public String hello(HttpServletResponse response) throws IOException {
        response.getWriter().write("hello");
        return "";
    }
}
