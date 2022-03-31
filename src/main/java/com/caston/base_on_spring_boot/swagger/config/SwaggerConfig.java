package com.caston.base_on_spring_boot.swagger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;

@EnableOpenApi
@Configuration
public class SwaggerConfig {
    /**
     * 配置swagger的Docket bean
     *
     * @return
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30) // 指定swagger3.0版本
                .groupName("caston") // 设置分组名
                .enable(true) //是设置swagger是否可用
                /*
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.caston.base_on_spring_boot.swagger.controller")) // 设置可访问controller
                .build()
                 */
                .apiInfo(createApiInfo());
    }

    /**
     * 配置swagger的ApiInfo bean
     *
     * @return
     */
    @Bean
    public ApiInfo createApiInfo() {
        return new ApiInfo("Swagger Spring Boot",
                "基于spring boot的swagger测试",
                "3.0",
                "",
                new Contact("caston", "", ""),
                "",
                "",
                new ArrayList());
    }
}
