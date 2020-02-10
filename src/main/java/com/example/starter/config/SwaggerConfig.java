package com.example.starter.config;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

/**
 * @author : bingrun.chiu
 **/
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket swaggerSpringMvcPlugin() {

        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = Lists.newArrayList();
        ticketPar.name("token").description("token")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build(); //header中的token参数非必填，传空也可以
        pars.add(ticketPar.build());    //根据每个方法名也知道当前方法在设置什么参数

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .build()
                .globalOperationParameters(pars)
                .apiInfo(apiInfo());
    }

    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("接口文档")
                .description("获取token的地址: http://localhost:9000/swagger-ui.html#/login-controller/LogInUsingPOST")
                .version("2")
                .build();
    }

}
