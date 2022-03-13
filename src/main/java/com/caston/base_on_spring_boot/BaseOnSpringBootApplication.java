package com.caston.base_on_spring_boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@SpringBootApplication
public class BaseOnSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseOnSpringBootApplication.class, args);
    }

}
