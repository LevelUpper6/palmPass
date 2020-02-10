package com.example.starter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : bingrun.chiu
 * @description: 公共
 * @date: 2020/2/3 12:55
 **/
@RestController
public class CommonController {

    @RequestMapping("/actuator/info")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html"); // 将Eureka的info页面重定向到swagger页面,方便测试.
    }
}
