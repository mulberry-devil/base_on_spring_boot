package com.caston.base_on_spring_boot;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.caston.base_on_spring_boot.ehcache.entity.EhcacheUser;
import com.caston.base_on_spring_boot.ehcache.service.EhcacheService;
import com.caston.base_on_spring_boot.jjwt.utils.JWTUtil;
import io.jsonwebtoken.Claims;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
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
        pc.setParent("com.caston.base_on_spring_boot.springsecurity");
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

    @Test
    void ehcache() {
//        CacheManager cacheManager = CacheManager.create();
//        Cache ehcache = cacheManager.getCache("HelloEhcache");
//        Element element = new Element("key1", "value1");
//        ehcache.put(element);
//        System.out.println(ehcache.get("key1"));
//        cacheManager.shutdown();
    }

    @Resource
    private EhcacheService ehcacheService;
    @Resource
    private EhCacheCacheManager cacheManager;

    @Test
    void getEhcache() {
        ehcacheService.get("111");
        System.out.println("---------------------");
        ehcacheService.get("111");
        ehcacheService.getById("111");
        Cache helloEhcache = cacheManager.getCache("users");
        EhcacheUser ehcacheUser = helloEhcache.get("userid:111", EhcacheUser.class);
        System.out.println(ehcacheUser);
        System.out.println(helloEhcache);
    }

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void redis(){
//        redisTemplate.opsForValue().set("key2","value2");
        System.out.println(redisTemplate.opsForValue().get("key1"));
    }
}
