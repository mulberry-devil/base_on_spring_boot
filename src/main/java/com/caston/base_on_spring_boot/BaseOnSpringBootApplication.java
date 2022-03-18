package com.caston.base_on_spring_boot;

import com.caston.base_on_spring_boot.jjwt.utils.JWTUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@SpringBootApplication
public class BaseOnSpringBootApplication {

    public static void main(String[] args) {
        JWTUtil.key();
        SpringApplication.run(BaseOnSpringBootApplication.class, args);
    }

}
