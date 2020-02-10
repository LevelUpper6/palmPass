package com.example.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableCaching // 开启缓存
@SpringBootApplication
@EnableDiscoveryClient
public class StarterApplication {
	public static void main(String[] args) {
		SpringApplication.run(StarterApplication.class, args);
	}
}
