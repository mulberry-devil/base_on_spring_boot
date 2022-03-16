package com.caston.base_on_spring_boot;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.caston.base_on_spring_boot.jjwt.utils.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.Key;
import java.util.Date;

@SpringBootTest
class BaseOnSpringBootApplicationTests {
    @Test
    void contextLoads() {
        JWTUtil.key();
        String jwt = JWTUtil.generate("31352512");
        System.out.println(jwt);
        Claims claim = JWTUtil.parse(jwt);
        Date issuedAt = claim.getIssuedAt();
        Object uid = claim.get("UID");
        System.out.println(issuedAt.getTime());
        System.out.println(uid);
    }

    @Test
    void generateCode() {
        // 1、创建代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // 2、全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("caston");
        gc.setOpen(false); //生成后是否打开资源管理器
        gc.setServiceName("%sService");    //去掉Service接口的首字母I

        mpg.setGlobalConfig(gc);
        // 3、数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://112.74.87.145:3306/base_spring_boot?serverTimezone=UTC&characterEncoding=utf-8");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("cqs_root");
        dsc.setPassword("cqs9527");
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);
        // 4、包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.caston.base_on_spring_boot.shardingsphere");
        pc.setEntity("entity"); //此对象与数据库表结构一一对应，通过 DAO 层向上传输数据源对象。
        mpg.setPackageInfo(pc);
        // 5、策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
        strategy.setEntityLombokModel(true); // lombok
        strategy.setRestControllerStyle(true); //restful api风格控制器
        mpg.setStrategy(strategy);
        // 6、执行
        mpg.execute();
    }
}
