package com.example.starter.config;

import com.example.starter.service.PersonService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * @author : bingrun.chiu
 * @description: Spring启动后初始化SDK
 * @date: 2020/1/9 10:49
 **/
@Log4j2
@Service
public class Initiate implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private SdkTools sdkTools;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("SDK初始化...");
//        if (contextRefreshedEvent.getApplicationContext().getParent() == null) {
            //需要执行的代码
            if (sdkTools.initialize()) {
                log.info("SDK初始化成功");
            } else {
                log.error("SDK初始化失败");
            }
//        }
    }
}
