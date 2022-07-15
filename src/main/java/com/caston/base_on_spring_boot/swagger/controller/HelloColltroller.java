package com.caston.base_on_spring_boot.swagger.controller;

import com.caston.base_on_spring_boot.swagger.entity.Hello;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "在swagger-ui为该类定义名字")
@RestController
@RequestMapping("/swagger")
public class HelloColltroller {
    @ApiOperation(value = "在swagger-ui为该类中的方法定义名字", notes = "方法的备注说明")
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @ApiOperation(value = "测试参数说明注解")
    @PostMapping("/search/{age}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "姓名", required = true, paramType = "query"),
            @ApiImplicitParam(name = "age", value = "年龄", required = true, paramType = "path", dataType = "Integer")
    })
    public String search(String name, @PathVariable Integer age) {
        return name + "：" + age;
    }

    @ApiOperation("测试实体类说明")
    @GetMapping("/get")
    public String get(Hello hello) {
        return hello.getName() + "：" + hello.getAge();
    }

    @ApiOperation("测试响应码描述信息")
    @GetMapping("/load/{name}")
    @ApiResponses({
            @ApiResponse(code = 408, message = "返回值说明1"),
            @ApiResponse(code = 409, message = "返回值说明2")
    })
    public Hello load(@PathVariable String name) {
        return new Hello(name, 1);
    }

    @ApiOperation("测试文件上传信息")
    @PostMapping("/importFile")
    public String importFile(@RequestPart MultipartFile file) {
        return file.getOriginalFilename();
    }
}
