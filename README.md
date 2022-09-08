# 基于spring boot的学习
## Swagger

### 安装步骤

1. 加入依赖

   ```xml
   <!-- https://mvnrepository.com/artifact/io.springfox/springfox-boot-starter -->
   <dependency>
       <groupId>io.springfox</groupId>
       <artifactId>springfox-boot-starter</artifactId>
       <version>3.0.0</version>
   </dependency>
   ```

2. 在启动类开启`Swagger`

   ```java
   @EnableOpenApi
   ```

3. 打开`swagger-ui`

   - `http://localhost:8080/swagger-ui/`

### 注解说明

- `@Api`
  - `tags`：在swagger-ui为该类定义名字
- `@ApiOperation`
  - `value`：在swagger-ui为该类中的方法添加说明
  - `notes`：方法的备注说明
- `@ApiImplicitParams`：为参数添加说明
  - `@ApiImplicitParam`
    - `name`：指定对应参数
    - `value`：在swagger-ui为该参数添加说明
    - `required`：参数是否为必填
    - `paramType`：参数类型
      - `@RequestHeader 对应 header`
      - `@RequestParam 对应 query`
      - `@PathVariable 对应 path`
    - `dataType`：参数类型，默认为`String`
    - `defaultValue`：参数默认值
- `@ApiModel`：在swagger-ui为实体类定义名字
- `@ApiModelProperty`：在swagger-ui为实体类属性定义名字
- `@ApiResponses`：描述响应码信息
  - `@ApiResponse`
    - `code`：响应码
    - `message`：对应响应码信息

### 配置

- 设置分组即再增加`createRestApi`和`createApiInfo`，然后再设置可访问的`controller`包

```java
package com.caston.base_on_spring_boot.swagger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;

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
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.caston.base_on_spring_boot.swagger.controller")) // 设置可访问controller
                .build()
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
```

## JJWT

### 引用依赖

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.2</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.2</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId> <!-- or jjwt-gson if Gson is preferred -->
    <version>0.11.2</version>
    <scope>runtime</scope>
</dependency>
<!-- Uncomment this next dependency if you are using JDK 10 or earlier and you also want to use 
     RSASSA-PSS (PS256, PS384, PS512) algorithms.  JDK 11 or later does not require it for those algorithms:
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk15on</artifactId>
    <version>1.60</version>
    <scope>runtime</scope>
</dependency>
-->
```

### 知识点

- 对称加密：明文 + 私钥 + 算法 = 密文 <==> 密文 + 私钥 + 算法 = 明文
- 非对称加密：明文 + 公钥 + 算法 = 密文 <==> 密文 + 私钥 + 算法 = 明文
- `JWT`组成，eg：`eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKb2UifQ.KypJi92tGD-THQRI7Sq4DXmdN1YGmWXMucCdo_qUvfc`
  - `header`头部分：指定加密算法
  - `body`签名明文
    - `sub`：主题
    - `iss`：签发者
    - `aud`：接收方
    - `iat`：签发时间
    - `exp`：过期时间
    - `nbf`：定义生效时间
    - `jti`：`jwt`的唯一身份标识，主要用来作为一次性token，从而避免重放攻击
    - 自定义字段
  - 签名密文：对`body`签名信息按照`header`指定的算法进行加密后的密文字符串

### 快速使用

```java
// 生成jwt
Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 创建密钥
String secretKey = Encoders.BASE64.encode(key.getEncoded()); // 将密钥以BASE64编码保存
Map<String, String> claim = new HashMap<>();
claim.put("UID", uid);
Date current = new Date();
Date expDate = new Date(current.getTime() + expTime);
Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWTUtil.secretKey));
String jwt = Jwts.builder()
    .setClaims(claim) // 自定义明文信息
    .setIssuedAt(current) // 签发时间
    .setExpiration(expDate) // 过期时间
    .signWith(key) // 指定密钥
    .compact();
// 解析jwt
Jws<Claims> jws = null;
try {
	jws = Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(jwt);
} catch (JwtException ex) {
	ex.printStackTrace();
}
// 读取jwt中明文信息
Date issuedAt = claim.getIssuedAt();
Object uid = claim.get("UID");
```

### 引用场景

#### 登录控制

- 流程

  - 首次登录时产生`jwt`后随着`response`的`header`或者`body`传递到前端

  - 前端`response`拦截器拦截请求后将`jwt`存到本地`session`、`cookies`、`LocalStorage`或者其他

  - 之后每次前端向后端请求时从本地获取`jwt`并携带`jwt`到请求头进行请求

  - 后端拦截后对`jwt`进行处理，并且每次生成新的`jwt`避免使用过程中失效

- 知识点

  - spring boot拦截器

    - 写一个拦截类实现`HandlerInterceptor`重写方法

      ```java
      package com.caston.base_on_spring_boot.jjwt.interceptor;
      
      import org.springframework.web.servlet.HandlerInterceptor;
      
      import javax.servlet.http.HttpServletRequest;
      import javax.servlet.http.HttpServletResponse;
      
      public class LoginInterceptor implements HandlerInterceptor {
          @Override
          public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
              return HandlerInterceptor.super.preHandle(request, response, handler);
          }
      }
      ```

    - 写一个配置类（实现`WebMvcConfigurer`），将拦截类配置进去

      ```java
      package com.caston.base_on_spring_boot.jjwt.config;
      
      import com.caston.base_on_spring_boot.jjwt.interceptor.LoginInterceptor;
      import org.springframework.context.annotation.Configuration;
      import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
      import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
      
      @Configuration
      public class WebConfig implements WebMvcConfigurer {
          @Override
          public void addInterceptors(InterceptorRegistry registry) {
              registry.addInterceptor(new LoginInterceptor()) // 配置拦截类
                  .addPathPatterns("/jjwt/**"); // 拦截规则
          }
      }
      ```

  - 当需要操作请求头时，需要使用`ResponseEntity`返回给前端

    ```java
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "用户id", paramType = "path"),
        @ApiImplicitParam(name = "password", value = "密码", paramType = "path")
    })
    @GetMapping("/login/{userId}/{password}")
    public ResponseEntity login(@PathVariable String userId, @PathVariable String password) {
        String jwt = JWTUtil.generate(userId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("admin-token",jwt);
        return new ResponseEntity("可将返回对象或者JSON放入此位置",headers, HttpStatus.OK);
    }
    ```

#### 接口授权

通过token来对用户可以调用哪些接口进行授权

#### url有效期控制

通过token中的有效期来控制时间

## MybatisPlus

### 引用依赖

```xml
<!-- mybatis-plus依赖 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.4.2</version>
</dependency>
<!-- MySQL依赖 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
<!-- druid依赖 -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.22</version>
</dependency>
<!--mybatis-plus 代码生成器-->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-generator</artifactId>
    <version>3.4.1</version>
</dependency>
<dependency>
    <groupId>org.apache.velocity</groupId>
    <artifactId>velocity-engine-core</artifactId>
    <version>2.0</version>
</dependency>
```

### 知识点讲解

- 数据源配置以及数据初始化（在没有分库分表的情况下）

  ```yaml
  spring:
    application:
      name: base_on_spring_boot # 应用名称
    datasource:
      druid:
        name: base_spring_boot
        url: jdbc:mysql://localhost:3306/base_spring_boot?serverTimezone=UTC&characterEncoding=utf-8
        driver-class-name: com.mysql.cj.jdbc.Driver
        username: root
        password: 123456
        filters: stat # 监控统计
        initial-size: 2 # 初始化连接
        max-idle: 10 # 最大空闲连接数
        min-idle: 1 # 最小空闲连接数
        max-active: 20 # 最大活动连接
        max-wait: 60000 # 获取连接超时的等待时间
        time-between-eviction-runs-millis: 60000 # 间隔多久进行一次检测，检测需要关闭的空闲连接
        min-evictable-idle-time-millis: 300000 # 一个连接在池中最小生产的空间
        validation-query: SELECT 'x' # 验证连接有效与否的SQL
        test-while-idle: true # 指明连接是否被空闲连接回收器（如果有）进行检验，如果检验失败，则连接将被从池中去除
        test-on-borrow: false # 借出连接时不要测试，否则影响性能
      # sql数据初始化
      schema:
        - classpath*:sql/*.sql # 指定建表语句sql文件，需要提前建好
      data:
        - classpath*:sql/data/*.sql # 指定数据sql文件，需要提前建好
      initialization-mode: always
  mybatis-plus:
    configuration:
      log-impl: org.apache.ibatis.logging.stdout.StdOutImpl # 打印执行的sql语句
    mapper-locations:
      - classpath:com/caston/base_on_spring_boot/*/mapper/xml/*.xml # 指定xml所在位置
  ```

- 因为用代码生成器生成的xml不在resource中，所以需要在pom.xml中指定加载路径

  ```xml
  <build>
      <resources>
          <resource>
              <!--   描述存放资源的目录，该路径相对POM路径-->
              <directory>src/main/java</directory>
              <includes>
                  <include>**/*.xml</include>
              </includes>
          </resource>
      </resources>
  </build>
  ```

  

- `Mapper`继承`BaseMapper<实体类名>`，`Service`继承`IService<实体类名>`，`ServiceImpl`继承`ServiceImpl<Mapper类名, 实体类名>`

### 代码生成器

```java
// 1、创建代码生成器
AutoGenerator mpg = new AutoGenerator();
// 2、全局配置
GlobalConfig gc = new GlobalConfig();
String projectPath = System.getProperty("user.dir");
gc.setOutputDir(projectPath + "/src/main/java");
gc.setAuthor("caston");
gc.setOpen(false); //生成后是否打开资源管理器
gc.setServiceName("%sService");	//去掉Service接口的首字母I
mpg.setGlobalConfig(gc);
// 3、数据源配置
DataSourceConfig dsc = new DataSourceConfig();
dsc.setUrl("jdbc:mysql://localhost:3306/base_spring_boot?serverTimezone=UTC&characterEncoding=utf-8");
dsc.setDriverName("com.mysql.cj.jdbc.Driver");
dsc.setUsername("root");
dsc.setPassword("123456");
dsc.setDbType(DbType.MYSQL);
mpg.setDataSource(dsc);
// 4、包配置
PackageConfig pc = new PackageConfig();
pc.setParent("com.caston.base_on_spring_boot.mybatisplus");
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
```

### 分页插件

- 配置分页插件

  ```java
  package com.caston.base_on_spring_boot.mybatisplus.config;
  
  import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
  import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
  import org.mybatis.spring.annotation.MapperScan;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  
  @Configuration
  @MapperScan("com.caston.base_on_spring_boot.mybatisplus.mapper")
  public class MybatisPlusConfig {
      /*
      分页插件
       */
      @Bean
      public MybatisPlusInterceptor mybatisPlusInterceptor() {
          MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
          mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
          return mybatisPlusInterceptor;
      }
  }
  ```

- 分页举例

  ```java
  Page<User> page = new Page<>(current,pageSize);
  Page<User> userPage = userMapper.selectPage(page, new QueryWrapper<>());
  ```

## Quartz

### 引用依赖

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-quartz</artifactId>
</dependency>
```

1. 编写定时方法并注入容器中

2. 编写配置类文件

   ```java
   package com.caston.base_on_spring_boot.quartz.config;
   
   import com.caston.base_on_spring_boot.quartz.service.QuartzService;
   import org.quartz.CronTrigger;
   import org.springframework.beans.factory.annotation.Qualifier;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
   import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
   import org.springframework.scheduling.quartz.SchedulerFactoryBean;
   
   import java.util.Objects;
   
   /**
    * 有多少个定时方法就要写多少个job,trigger
    * 将所有job,trigger注入schedule中
    */
   @Configuration
   public class QuartzConfig {
       /**
        * 该方法设置定时执行的任务
        * <p>
        * TargetObject：指定需要定时执行的那个对象
        * targetMethod: 指定需要定时执行的方法
        * concurrent：
        * false:多个job不会并发执行,设置此次定时完成再执行下一次
        * true:多个job并发执行
        *
        * @param quartzService
        * @return
        */
       @Bean(name = "detailFactoryBean1")
       public MethodInvokingJobDetailFactoryBean detailFactoryBean1(QuartzService quartzService) {
           MethodInvokingJobDetailFactoryBean factoryBean = new MethodInvokingJobDetailFactoryBean();
           factoryBean.setTargetObject(quartzService);
           factoryBean.setTargetMethod("quartz1");
           factoryBean.setConcurrent(false);
           return factoryBean;
       }
   
       /**
        * 该方法设置触发器（定时任务的执行方式）
        *
        * @param detailFactoryBean
        * @return
        */
       @Bean("cronTriggerFactoryBean1")
       public CronTriggerFactoryBean cronTriggerFactoryBean1(@Qualifier("detailFactoryBean1") MethodInvokingJobDetailFactoryBean detailFactoryBean) {
           CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
           trigger.setJobDetail(Objects.requireNonNull(detailFactoryBean.getObject()));
           try {
               trigger.setCronExpression("0/5 * * * * ? ");
           } catch (Exception e) {
               e.printStackTrace();
           }
           return trigger;
       }
   
       @Bean(name = "detailFactoryBean2")
       public MethodInvokingJobDetailFactoryBean detailFactoryBean2(QuartzService quartzService) {
           MethodInvokingJobDetailFactoryBean factoryBean = new MethodInvokingJobDetailFactoryBean();
           factoryBean.setTargetObject(quartzService);
           factoryBean.setTargetMethod("quartz2");
           factoryBean.setConcurrent(false);
           return factoryBean;
       }
   
       @Bean("cronTriggerFactoryBean2")
       public CronTriggerFactoryBean cronTriggerFactoryBean2(@Qualifier("detailFactoryBean2") MethodInvokingJobDetailFactoryBean detailFactoryBean) {
           CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
           trigger.setJobDetail(Objects.requireNonNull(detailFactoryBean.getObject()));
           try {
               trigger.setCronExpression("0/5 * * * * ? ");
           } catch (Exception e) {
               e.printStackTrace();
           }
           return trigger;
       }
   
       /**
        * 该方法是通过调度工厂执行定时任务，可以写多个trigger
        *
        * @param cronTrigger1
        * @param cronTrigger2
        * @return
        */
       @Bean
       public SchedulerFactoryBean schedulerFactoryBean2(@Qualifier("cronTriggerFactoryBean1") CronTrigger cronTrigger1, @Qualifier("cronTriggerFactoryBean2") CronTrigger cronTrigger2) {
           SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
           schedulerFactoryBean.setTriggers(cronTrigger1, cronTrigger2);
           return schedulerFactoryBean;
       }
   }
   ```

## ShardingSphere

### 引用依赖

```xml
<dependency>
    <groupId>org.apache.shardingsphere</groupId>
    <artifactId>shardingsphere-jdbc-core-spring-boot-starter</artifactId>
    <version>5.0.0-alpha</version>
</dependency>
<!-- 导入druid-spring-boot-starter后启动会报错 -->
<!--        <dependency>-->
<!--            <groupId>com.alibaba</groupId>-->
<!--            <artifactId>druid-spring-boot-starter</artifactId>-->
<!--            <version>1.1.22</version>-->
<!--        </dependency>-->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.2.8</version>
</dependency>
```

### 分库分表配置

```yaml
spring:
  # ShardingSphere配置
  shardingsphere:
    enabled: true # 可在此开启或关闭
    ## 分库-读写分离
    datasource:
      common:
        type: com.alibaba.druid.pool.DruidDataSource
        driver-class-name: com.mysql.cj.jdbc.Driver
        password: 密码
      names: master,slave0,slave1 # 指定三个数据源名称
      master: # 配置第一个数据源
        url: jdbc:mysql://ip:3306/base_spring_boot?serverTimezone=UTC&characterEncoding=utf-8
        username: 账号
      slave0: # 配置第二个数据源
        url: jdbc:mysql://ip:3306/wechat_netty?serverTimezone=UTC&characterEncoding=utf-8
        username: 账号
      slave1: # 配置第三个数据源
        url: jdbc:mysql://ip:3306/base_spring_1?serverTimezone=UTC&characterEncoding=utf-8
        username: 账号
    rules:
      ## 分库-读写分离
      replica-query:
        data-sources:
          ms: # 定义数据源名字
            primary-data-source-name: master # 指定主数据源
            replica-data-source-names: slave0,slave1 # 指定从数据源
            load-balancer-name: round_robin # 负载均衡算法名称
        load-balancers:
          <上面定义的负载均衡算法名称>:
            type: ROUND_ROBIN # 负载均衡算法配置
            props:
              workId: 1 # 负载均衡算法属性配置
      ## 分表
      sharding:
        bindingTables:
          - <表名>
        tables:
          user:
            actualDataNodes: <上面定义数据源名字>.<表名>_$->{0..2} # 标准分片表配置：由数据源 + 表名组成，多个表以逗号分割
            tableStrategy:
              standard: # 配置分片场景
                shardingColumn: <表中列名> # 分片列名称
                shardingAlgorithmName: table-inline # 分片算法名称
        keyGenerators: # 雪花算法配置
          snowflake:
            type: SNOWFLAKE
            props:
              worker-id: 123
        shardingAlgorithms: # 自定义分片算法
          <上面定义的分片算法名称>:
            type: INLINE
            props:
              algorithm-expression: <表名>_$->{<表中列名> % 3}
    props:
      sql-show: true # 打印sql
```

## Spring Security

### 引用依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 认证以及使用注解配置权限

1. 创建登录认证以及权限表类，实现`UserDetailsService`

   ```java
   package com.caston.base_on_spring_boot.springsecurity.service;
   
   import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
   import com.caston.base_on_spring_boot.springsecurity.entity.LoginTable;
   import com.caston.base_on_spring_boot.springsecurity.entity.Permission;
   import com.caston.base_on_spring_boot.springsecurity.entity.Role;
   import com.caston.base_on_spring_boot.springsecurity.mapper.LoginTableMapper;
   import com.caston.base_on_spring_boot.springsecurity.mapper.PermissionMapper;
   import com.caston.base_on_spring_boot.springsecurity.mapper.RoleMapper;
   import org.springframework.security.core.GrantedAuthority;
   import org.springframework.security.core.authority.SimpleGrantedAuthority;
   import org.springframework.security.core.userdetails.User;
   import org.springframework.security.core.userdetails.UserDetails;
   import org.springframework.security.core.userdetails.UserDetailsService;
   import org.springframework.security.core.userdetails.UsernameNotFoundException;
   import org.springframework.stereotype.Service;
   
   import javax.annotation.Resource;
   import java.util.ArrayList;
   import java.util.List;
   
   @Service
   public class LoginSecurityService implements UserDetailsService {
       @Resource
       private LoginTableMapper loginTableMapper;
       @Resource
       private RoleMapper roleMapper;
       @Resource
       private PermissionMapper permissionMapper;
   
       @Override
       public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
           LoginTable user = loginTableMapper.selectOne(new LambdaQueryWrapper<LoginTable>().eq(LoginTable::getUsername, username));
           if (user == null) {
               throw new UsernameNotFoundException("用户不存在");
           }
           ArrayList<GrantedAuthority> authorities = new ArrayList<>(); // 权限List
           List<Role> roleList = roleMapper.findRoleListByUserId(user.getId());
           roleList.forEach(i -> {
               authorities.add(new SimpleGrantedAuthority("ROLE_" + i.getRoleKeyword())); // 将权限名，角色名添加至权限List中
               List<Permission> permissionList = permissionMapper.findPermissionByRole(i.getId());
               permissionList.forEach(j -> {
                   authorities.add(new SimpleGrantedAuthority(j.getPermissionKeyword())); // 将权限名，角色名添加至权限List中
               });
           });
           //  数据库密码应为经过 new BCryptPasswordEncoder().encode("密码") 编译后的
           UserDetails userDetails = new User(username, user.getPassword(), authorities);
           return userDetails;
       }
   }
   ```

2. 配置类

   ```java
   package com.caston.base_on_spring_boot.springsecurity.config;
   
   import com.caston.base_on_spring_boot.springsecurity.service.LoginSecurityService;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
   import org.springframework.security.config.annotation.web.builders.HttpSecurity;
   import org.springframework.security.config.annotation.web.builders.WebSecurity;
   import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
   import org.springframework.security.core.Authentication;
   import org.springframework.security.core.AuthenticationException;
   import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
   import org.springframework.security.web.authentication.AuthenticationFailureHandler;
   import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
   import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
   
   import javax.annotation.Resource;
   import javax.servlet.ServletException;
   import javax.servlet.http.HttpServletRequest;
   import javax.servlet.http.HttpServletResponse;
   import java.io.IOException;
   
   @Configuration
   @EnableGlobalMethodSecurity(prePostEnabled = true) // 开启注解配置权限
   public class SecurityConfig extends WebSecurityConfigurerAdapter {
   
       @Resource
       private LoginSecurityService loginSecurityService;
   
       @Bean
       public BCryptPasswordEncoder bCryptPasswordEncoder() {
           return new BCryptPasswordEncoder();
       }
   
       @Override
       protected void configure(HttpSecurity http) throws Exception {
           http.userDetailsService(loginSecurityService); // 自定义认证对象，单个时可写可不写，一般是多个service时才写
           http.authorizeRequests() // 开启登录配置
                   //  .antMatchers("/loginTable/findAll").hasRole("admin") // 访问接口授权，此例说明需要角色为admin
                   .antMatchers("/login").permitAll()
                   .anyRequest().authenticated() // 其他所有请求，只需要登录即可，其他所有请求，只需要登录即可，在使用数据库配置权限时需要注释掉
                   .and().formLogin()
                   //  .loginPage("/login.html") // 自定义登录界面
                   .loginProcessingUrl("/login") // 登录处理接口
                   .usernameParameter("username") // 定义登陆时的用户名的key，默认为username
                   .passwordParameter("password") // 定义登陆时的密码的key，默认为password
                   //  .successForwardUrl("") // 登录成功跳转url，为post请求
                   //  .defaultSuccessUrl("") // 登录成功跳转url，为get请求
                   .successHandler(new AuthenticationSuccessHandler() {
                       @Override
                       public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                           System.out.println("1111111111111111111111111111111111111111111111");
                       }
                   }) // 登录成功处理器
                   .failureHandler(new AuthenticationFailureHandler() {
                       @Override
                       public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                           System.out.println("22222222222222222222222222222222222222222");
                       }
                   }) // 登录失败处理器
                   .permitAll()
                   .and().logout()
                   .logoutUrl("/logout") // 退出登录接口
                   .logoutSuccessHandler(new LogoutSuccessHandler() {
                       @Override
                       public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                           System.out.println("333333333333333333333333333333333");
                       }
                   }) // 退出登录成功处理器
                   .permitAll()
                   .and().httpBasic().and().csrf().disable();
       }
   
       /*
       忽略swagger
        */
       @Override
       public void configure(WebSecurity web) throws Exception {
           web.ignoring().antMatchers("/swagger-ui/**")
                   .antMatchers("/v3/**")
                   .antMatchers("/swagger-resources/**");
       }
   }
   ```

3. 在控制层使用注解规定权限表中权限规则

   ```java
   @GetMapping("/findAll")
   @PreAuthorize("hasAuthority('USER_FINDALL')") // 配置权限，对应权限列表中的名
   public List<LoginTable> findAll() {
       List<LoginTable> users = loginTableService.list();
       return users;
   }
   @GetMapping("/findAge")
   @PreAuthorize("hasRole('ROLE_ADMIN')") // 配置角色，对应权限列表中的名
   public List<LoginTable> findAge() {
       List<LoginTable> users = loginTableService.list();
       return users;
   }
   ```

### 通过读取数据库进行授权

   1. 自定义权限类，将数据库权限和路径匹配，实现`GrantedAuthority`

      ```java
      package com.caston.base_on_spring_boot.springsecurity.service.security;
      
      import org.springframework.security.core.GrantedAuthority;
      
      public class MySimpleGrantedAuthority implements GrantedAuthority {
      
          private String authority;
      
          public String getPath() {
              return path;
          }
      
          private String path;
      
          public MySimpleGrantedAuthority(String authority) {
              this.authority = authority;
          }
      
          public MySimpleGrantedAuthority(String authority, String path) {
              this.authority = authority;
              this.path = path;
          }
      
          @Override
          public String getAuthority() {
              return authority;
          }
      }
      ```

   2. 自定义`service `来实现实时的权限认证

      ```java
      package com.caston.base_on_spring_boot.springsecurity.service.security;
      
      import org.apache.commons.lang3.StringUtils;
      import org.springframework.security.core.Authentication;
      import org.springframework.security.core.GrantedAuthority;
      import org.springframework.security.core.userdetails.UserDetails;
      import org.springframework.stereotype.Service;
      
      import javax.servlet.http.HttpServletRequest;
      import java.util.Collection;
      
      @Service
      public class AuthService {
          /**
           * 自定义权限授权
           *
           * @param request
           * @param authentication
           * @return true为放行，false代表拦截
           */
          public boolean auth(HttpServletRequest request, Authentication authentication) {
              Object principal = authentication.getPrincipal();
              // 没有登录时为空或者为匿名状态
              if (principal == null || "anonymousUser".equals(principal)) {
                  return false;
              }
              UserDetails userDetails = (UserDetails) principal;
              Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
              for (GrantedAuthority authority : authorities) {
                  MySimpleGrantedAuthority mySimpleGrantedAuthority = (MySimpleGrantedAuthority) authority;
                  String path = mySimpleGrantedAuthority.getPath();
                  String[] split = StringUtils.split(request.getRequestURI(), "?");
                  if (split[0].equals(path)) {
                      return true;
                  }
              }
              return false;
          }
      }
      ```

   3. 修改配置类

      ```java
      //@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启注解配置权限
      
      .anyRequest().access("@authService.auth(request,authentication)")
      //  .anyRequest().authenticated()
      ```

      完整代码

      ```java
      package com.caston.base_on_spring_boot.springsecurity.config;
      
      import com.caston.base_on_spring_boot.springsecurity.service.security.LoginSecurityService;
      import org.springframework.context.annotation.Bean;
      import org.springframework.context.annotation.Configuration;
      import org.springframework.security.config.annotation.web.builders.HttpSecurity;
      import org.springframework.security.config.annotation.web.builders.WebSecurity;
      import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
      import org.springframework.security.core.Authentication;
      import org.springframework.security.core.AuthenticationException;
      import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
      import org.springframework.security.web.authentication.AuthenticationFailureHandler;
      import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
      import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
      
      import javax.annotation.Resource;
      import javax.servlet.ServletException;
      import javax.servlet.http.HttpServletRequest;
      import javax.servlet.http.HttpServletResponse;
      import java.io.IOException;
      
      @Configuration
      //@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启注解配置权限
      public class SecurityConfig extends WebSecurityConfigurerAdapter {
      
          @Resource
          private LoginSecurityService loginSecurityService;
      
          @Bean
          public BCryptPasswordEncoder bCryptPasswordEncoder() {
              return new BCryptPasswordEncoder();
          }
      
          @Override
          protected void configure(HttpSecurity http) throws Exception {
              http.userDetailsService(loginSecurityService); // 自定义认证对象，单个时可写可不写，一般是多个service时才写
              http.authorizeRequests() // 开启登录配置
                      //  .antMatchers("/loginTable/findAll").hasRole("admin") // 访问接口授权，此例说明需要角色为admin
                      .antMatchers("/login").permitAll()
                      .anyRequest().access("@authService.auth(request,authentication)") // 自定义service 来实现实时的权限认证，@后可以使用bean中的任何对象，此参数与类中的方法参数名一样
      //                .anyRequest().authenticated() // 其他所有请求，只需要登录即可，在使用数据库配置权限时需要注释掉
                      .and().formLogin()
                      //  .loginPage("/login.html") // 自定义登录界面
                      .loginProcessingUrl("/login") // 登录处理接口
                      .usernameParameter("username") // 定义登陆时的用户名的key，默认为username
                      .passwordParameter("password") // 定义登陆时的密码的key，默认为password
                      //  .successForwardUrl("") // 登录成功跳转url，为post请求
                      //  .defaultSuccessUrl("") // 登录成功跳转url，为get请求
                      .successHandler(new AuthenticationSuccessHandler() {
                          @Override
                          public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                              System.out.println("1111111111111111111111111111111111111111111111");
                          }
                      }) // 登录成功处理器
                      .failureHandler(new AuthenticationFailureHandler() {
                          @Override
                          public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                              System.out.println("22222222222222222222222222222222222222222");
                          }
                      }) // 登录失败处理器
                      .permitAll()
                      .and().logout()
                      .logoutUrl("/logout") // 退出登录接口
                      .logoutSuccessHandler(new LogoutSuccessHandler() {
                          @Override
                          public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                              System.out.println("333333333333333333333333333333333");
                          }
                      }) // 退出登录成功处理器
                      .permitAll()
                      .and().httpBasic().and().csrf().disable();
          }
      
          /*
          忽略swagger
           */
          @Override
          public void configure(WebSecurity web) throws Exception {
              web.ignoring().antMatchers("/swagger-ui/**")
                      .antMatchers("/v3/**")
                      .antMatchers("/swagger-resources/**");
          }
      }
      ```

## Ehcache

### 引用依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<!-- https://mvnrepository.com/artifact/net.sf.ehcache/ehcache -->
<dependency>
    <groupId>net.sf.ehcache</groupId>
    <artifactId>ehcache</artifactId>
    <version>2.10.6</version>
</dependency>
```

### Ehcache理解

`Ehcache`类似`Map`集合，`CacheManager`管理着`Cache`，`Cache`又有很多`Element`，每次从`Cache`取出元素就像从`Map`取出一样

### 缓存使用

1. 编写`ehcache.xml`定义`cache name`，类似`map name`，已经定义各种缓存策略

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd">
       <!--
          diskStore：为缓存路径，ehcache分为内存和磁盘两级，此属性定义磁盘的缓存位置。参数解释如下：
          user.home – 用户主目录
          user.dir  – 用户当前工作目录
          java.io.tmpdir – 默认临时文件路径
        -->
       <diskStore path="java.io.tmpdir/Tmp_EhCache"/>
       <!--
          defaultCache：默认缓存策略，当ehcache找不到定义的缓存时，则使用这个缓存策略。只能定义一个。
        -->
       <!--
         name:缓存名称。
         maxElementsInMemory:缓存最大数目
         maxElementsOnDisk：硬盘最大缓存个数。
         eternal:对象是否永久有效，一但设置了，timeout将不起作用。
         overflowToDisk:是否保存到磁盘，当系统当机时
         timeToIdleSeconds:设置对象在失效前的允许闲置时间（单位：秒）。仅当eternal=false对象不是永久有效时使用，可选属性，默认值是0，也就是可闲置时间无穷大。
         timeToLiveSeconds:设置对象在失效前允许存活时间（单位：秒）。最大时间介于创建时间和失效时间之间。仅当eternal=false对象不是永久有效时使用，默认是0.，也就是对象存活时间无穷大。
         diskPersistent：是否缓存虚拟机重启期数据 Whether the disk store persists between restarts of the Virtual Machine. The default value is false.
         diskSpoolBufferSizeMB：这个参数设置DiskStore（磁盘缓存）的缓存区大小。默认是30MB。每个Cache都应该有自己的一个缓冲区。
         diskExpiryThreadIntervalSeconds：磁盘失效线程运行时间间隔，默认是120秒。
         memoryStoreEvictionPolicy：当达到maxElementsInMemory限制时，Ehcache将会根据指定的策略去清理内存。默认策略是LRU（最近最少使用）。你可以设置为FIFO（先进先出）或是LFU（较少使用）。
         clearOnFlush：内存数量最大时是否清除。
         memoryStoreEvictionPolicy:可选策略有：LRU（最近最少使用，默认策略）、FIFO（先进先出）、LFU（最少访问次数）。
         FIFO，first in first out，这个是大家最熟的，先进先出。
         LFU， Less Frequently Used，就是上面例子中使用的策略，直白一点就是讲一直以来最少被使用的。如上面所讲，缓存的元素有一个hit属性，hit值最小的将会被清出缓存。
         LRU，Least Recently Used，最近最少使用的，缓存的元素有一个时间戳，当缓存容量满了，而又需要腾出地方来缓存新的元素的时候，那么现有缓存元素中时间戳离当前时间最远的元素将被清出缓存。
      -->
       <defaultCache
               eternal="false"
               maxElementsInMemory="10000"
               maxElementsOnDisk="10000000"
               timeToIdleSeconds="120"
               timeToLiveSeconds="120"
               memoryStoreEvictionPolicy="LRU"/>
   
       <cache
               name="HelloEhcache"
               eternal="false"
               maxElementsInMemory="1000"
               overflowToDisk="false"
               timeToIdleSeconds="5"
               timeToLiveSeconds="5"
               memoryStoreEvictionPolicy="LRU"/>
   
   </ehcache>
   ```

2. 配置类中开启缓存以及配置生成策略

   ```java
   package com.caston.base_on_spring_boot.ehcache.config;
   
   import com.alibaba.fastjson.JSON;
   import org.springframework.cache.annotation.CachingConfigurerSupport;
   import org.springframework.cache.annotation.EnableCaching;
   import org.springframework.cache.interceptor.KeyGenerator;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.util.DigestUtils;
   
   import java.nio.charset.StandardCharsets;
   
   @Configuration
   @EnableCaching
   public class EhcacheConfig extends CachingConfigurerSupport {
       @Bean
       @Override
       public KeyGenerator keyGenerator() {
           return (target, method, params) -> {
               StringBuilder strBuilder = new StringBuilder();
               strBuilder.append(target.getClass().getName());
               strBuilder.append(":");
               strBuilder.append(method.getName());
               for (Object obj : params) {
                   if (obj != null) {
                       strBuilder.append(":");
                       strBuilder.append(obj.getClass().getName());
                       strBuilder.append(":");
                       strBuilder.append(JSON.toJSONString(obj));
                   }
               }
               //log.info("ehcache key str: " + strBuilder.toString());
               String md5DigestAsHex = DigestUtils.md5DigestAsHex(strBuilder.toString().getBytes(StandardCharsets.UTF_8));
               return md5DigestAsHex;
           };
       }
   }
   ```

3. 使用注解进行缓存

   - `@Cacheable`：对结果进行缓存
     
     - `value`：`ehcache.xml`定义的`cache name`
     - `key`：存放于缓存中的键
     - `keyGenerator`：使用配置类中键的自定义生成策略，`key`/`keyGenerator`二选一使用
     - `condition`：触发条件，只有满足条件的情况下才会加入缓存，默认为空
     
     ```java
     @Cacheable(value = "users", key = "'userid:' + #id", condition = "#id.length() > 10")
     public EhcacheUser get(String id) {
         System.out.println("测试是否走缓存");
         return map.get(id);
     }
     @Cacheable(value = "users", keyGenerator = "keyGenerator")
     public EhcacheUser getById(String id) {
         System.out.println("测试是否走缓存------");
         return map.get(id);
     }
     ```
     
   - `@CachePut`：不仅对结果进行缓存，还会执行方法的代码段
   
     ```java
     @CachePut(value = "users", key = "#id")
     public EhcacheUser getCachePut(String id) {
         System.out.println("测试是否走缓存");
         return map.get(id);
     }
     ```
   
   - `@CacheEvict`：删除缓存数据
   
     - `value`：`ehcache.xml`定义的`cache name`
     - `key`：存放于缓存中的键
   
     - `condition`：触发条件，只有满足条件的情况下才会加入缓存，默认为空
   
     - `allEntries`：`true`为删除所有缓存，默认为`false`
   
     ```java
     @CacheEvict(value = "users", key = "#id",allEntries = true)
     public void getCacheEvict(String id) {
         System.out.println("删除缓存");
     }
     ```

## Redis

### 引用依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
    <version>2.11.1</version>
</dependency>
```

### `yml`配置

```yaml
spring:
  redis:
	host: localhost
    # host: 192.168.56.100
    port: 6379
    lettuce:
      pool:
        max-active: 8 # 连接池最大连接数（使用负值表示没有限制）
        max-wait: -1 # 连接池最大阻塞等待时间（使用负值表示没有限制）
        max-idle: 10 # 连接池最大连接数（使用负值表示没有限制）
        min-idle: 2 # 连接池中的最小空闲连接
    timeout: 6000 # 连接超时时间（毫秒）
```

### 键值序列化配置

```java
/**
 * 序列化键值对
 * @param factory
 * @return
 */
@Bean
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    // 配置连接工厂
    template.setConnectionFactory(factory);
    // 序列化和反序列化redis的value值（默认使用JDK的序列化方式）
    Jackson2JsonRedisSerializer jacksonSerializer = new Jackson2JsonRedisSerializer(Object.class);
    ObjectMapper om = new ObjectMapper();
    // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
    om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
    // om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL); 过期，使用下面替代
    om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    jacksonSerializer.setObjectMapper(om);
    // 值采用json序列化
    template.setValueSerializer(jacksonSerializer);
    // 使用StringRedisSerializer来序列化和反序列化redis的key值
    template.setKeySerializer(new StringRedisSerializer());
    // 设置hash key 和value序列化模式
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(jacksonSerializer);
    template.afterPropertiesSet();
    return template;
}
```

### 使用缓存

1. 在`yml`中开启`redis`缓存，注释掉`ehcache`缓存

   ```yaml
   spring:
     cache:
       type: redis
   ```

2. 写配置文件，因为和`ehcache`配置文件继承同一个类，所以注释掉`ehcache`配置文件或者写一起

   ```java
   /**
    * 配置缓存管理器
    *
    * @param connectionFactory
    * @return
    */
   @Bean
   public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
       Jackson2JsonRedisSerializer jacksonSerializer = new Jackson2JsonRedisSerializer(Object.class);
       ObjectMapper om = new ObjectMapper();
       // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
       om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
       // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
       // om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL); 过期，使用下面替代
       om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
       jacksonSerializer.setObjectMapper(om);
       StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
       RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory)
               .cacheDefaults(config(60L, stringRedisSerializer, jacksonSerializer)) // 缓存默认过期时间
               .withCacheConfiguration("caston", config(120L, stringRedisSerializer, jacksonSerializer)) // 指定组缓存的过期时间
               .transactionAware()
               .build();
       return redisCacheManager;
   }
   /**
    * 将配置抽取出来，为了自定义组缓存过期时间的定义
    *
    * @param time
    * @param stringRedisSerializer
    * @param jacksonSerializer
    * @return
    */
   public RedisCacheConfiguration config(Long time, StringRedisSerializer stringRedisSerializer, Jackson2JsonRedisSerializer jacksonSerializer) {
       return RedisCacheConfiguration.defaultCacheConfig()
               .entryTtl(Duration.ofSeconds(time)) // 缓存失效
               // 设置key的序列化方式
               .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer))
               // 设置value的序列化方式
               .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jacksonSerializer))
               // 不缓存null值
               .disableCachingNullValues();
   }
   ```

3. 使用注解进行缓存，用法在`ehcache`有讲解

   ```java
   @Cacheable(cacheNames = "user", key = "#age")
   public Hello selectByPrimaryKey(Integer age) {
       System.out.println("222222222222222222");
       return new Hello("caston", age);
   }
   ```

### 分布式锁（使用`redisson`）

1. 引用依赖

   ```xml
   <dependency>
       <groupId>org.redisson</groupId>
       <artifactId>redisson-spring-boot-starter</artifactId>
       <version>3.13.6</version>
   </dependency>
   ```

2. 引用`yml`配置文件

   ```yaml
   spring:
     redis:
       redisson:
     	  file: classpath:redisson.yml
   ```

   `redisson.yml`：可配集群

   ```yaml
   # 单节点配置
   singleServerConfig:
     # 连接空闲超时，单位：毫秒
     idleConnectionTimeout: 10000
     # 连接超时，单位：毫秒
     connectTimeout: 10000
     # 命令等待超时，单位：毫秒
     timeout: 3000
     # 命令失败重试次数,如果尝试达到 retryAttempts（命令失败重试次数） 仍然不能将命令发送至某个指定的节点时，将抛出错误。
     # 如果尝试在此限制之内发送成功，则开始启用 timeout（命令等待超时） 计时。
     retryAttempts: 3
     # 命令重试发送时间间隔，单位：毫秒
     retryInterval: 1500
     # 密码
     password:
     # 单个连接最大订阅数量
     subscriptionsPerConnection: 5
     # 客户端名称
     clientName: myredis
     # 节点地址
     address: redis://localhost:6379
     # 发布和订阅连接的最小空闲连接数
     subscriptionConnectionMinimumIdleSize: 1
     # 发布和订阅连接池大小
     subscriptionConnectionPoolSize: 50
     # 最小空闲连接数
     connectionMinimumIdleSize: 32
     # 连接池大小
     connectionPoolSize: 64
     # 数据库编号
     database: 0
     # DNS监测时间间隔，单位：毫秒
     dnsMonitoringInterval: 5000
   # 线程池数量,默认值: 当前处理核数量 * 2
   #threads: 0
   # Netty线程池数量,默认值: 当前处理核数量 * 2
   #nettyThreads: 0
   # 编码
   codec: !<org.redisson.codec.JsonJacksonCodec> {}
   # 传输模式
   transportMode : "NIO"
   ```

3. 分布式锁案例

   ```java
   @ApiOperation("测试添加分布式锁后的超卖现象")
   @GetMapping("/buy")
   public String buy() {
       RLock lock = null;
       try {
           lock = redissonClient.getLock("lock"); // 获取锁
           if (lock.tryLock(3, TimeUnit.SECONDS)) { // 重新尝试获取锁
               RAtomicLong buyBefore = redissonClient.getAtomicLong(KEY);
               if (Objects.isNull(buyBefore)) {
                   System.out.println("未找到" + KEY + "的库存信息~");
                   return "暂未上架～";
               }
               long buyBeforeL = buyBefore.get();
               if (buyBeforeL > 0) {
                   Long buyAfter = buyBefore.decrementAndGet();
                   System.out.println("剩余图书==={" + buyAfter + "}");
                   return "购买成功～";
               } else {
                   System.out.println("库存不足～");
                   return "库存不足～";
               }
           } else {
               System.out.println("获取锁失败～");
           }
       } catch (Exception e) {
           e.printStackTrace();
       } finally {
           //如果当前线程保持锁定则解锁
           if (null != lock && lock.isHeldByCurrentThread()) {
               lock.unlock(); // 缩放锁
           }
       }
       return "系统错误～";
   }
   ```

### 订阅发布

1. 监听配置

   ```java
   /**
    * 配置redis监听
    *
    * @param factory
    * @return
    */
   @Bean
   public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory factory) {
       RedisMessageListenerContainer container = new RedisMessageListenerContainer();
       container.setConnectionFactory(factory);
       return container;
   }
   ```

2. 订阅者（监听类），可写多个，互不影响

   ```java
   package com.caston.base_on_spring_boot.redis.listener;
   
   import org.springframework.data.redis.connection.Message;
   import org.springframework.data.redis.connection.MessageListener;
   import org.springframework.data.redis.listener.ChannelTopic;
   import org.springframework.data.redis.listener.RedisMessageListenerContainer;
   import org.springframework.stereotype.Component;
   
   @Component
   public class RedisSubListener implements MessageListener {
   
       public RedisSubListener(RedisMessageListenerContainer listenerContainer) {
           listenerContainer.addMessageListener(this, new ChannelTopic("caston")); // 对指定通道进行监听
           listenerContainer.addMessageListener(this, new ChannelTopic("chen"));
       }
   
       @Override
       public void onMessage(Message message, byte[] bytes) {
           System.out.println(getClass().getName() + ":" + "channel:" + new String(bytes) + ":" + message.toString());
       }
   }
   ```

3. 发布者发布

   ```java
   @PostMapping("/send2Redis")
   public void send2Redis(String message) {
       redisTemplate.convertAndSend("caston", message);
   }
   ```

## RabbitMQ

### 引用依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

### `yml`文件配置

```yaml
spring:
  rabbitmq:
    addresses: localhost
    port: 5672
    username: guest
    password: guest
```

### 生产者

```java
package com.caston.base_on_spring_boot.rabbitmq.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rabbitmq")
public class RabbitController {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @ApiOperation(value = "点对点发送", notes = "根据路由键向队列发送消息")
    @GetMapping("/point2point")
    public void point2point(String message) {
        rabbitTemplate.convertAndSend("point", message);
    }

    @ApiOperation(value = "点对点发送", notes = "根据路由键向队列发送消息但有多个消费者，随机一个消费者去消费信息")
    @GetMapping("/point2points")
    public void point2points() {
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend("points", "work模型" + i);
        }
    }

    @ApiOperation(value = "广播", notes = "与点对点不同的是，广播是将消息发送到交换机，再由交换机发送到交换机绑定的所有队列，和路由键没有关系")
    @GetMapping("/fanout")
    public void fanout(String message) {
        rabbitTemplate.convertAndSend("fanoutExchange", "log", message);
    }

    @ApiOperation(value = "根据路由键广播", notes = "将消息发送到交换机，再根据路由键由交换机发送到交换机绑定的队列")
    @GetMapping("/directs")
    public void directs(String message) {
        rabbitTemplate.convertAndSend("directsExchange", "directsKey1", message);
    }

    @ApiOperation(value = "根据路由键规则广播", notes = "将消息发送到交换机，再根据路由键规则由交换机发送到交换机绑定的队列")
    @GetMapping("/topic")
    public void topic(String message) {
        rabbitTemplate.convertAndSend("topicExchange", "topicKey.topic.topic", message);
    }
}
```

### 消费者

```java
package com.caston.base_on_spring_boot.rabbitmq.customer;

import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Component
public class Customer {
    // 点对点模式
    @RabbitListener(queuesToDeclare = {@Queue("point"), @Queue("points")})
    @RabbitHandler
    public void receivel1(String message) {
        System.out.println("message1：" + message);
    }

    @RabbitListener(queuesToDeclare = @Queue("points"))
    @RabbitHandler
    public void receivel2(String message) {
        System.out.println("message2：" + message);
    }

    @RabbitListener(queuesToDeclare = @Queue("points"))
    @RabbitHandler
    public void receivel3(String message) {
        System.out.println("message3：" + message);
    }

    // =========================================================================================

    // 广播模式
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue, // 创建临时队列
                    exchange = @Exchange(value = "fanoutExchange", type = "fanout")) // 绑定交换机
    })
    @RabbitHandler
    public void receive1(String message) {
        System.out.println("message1 = " + message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "fanoutExchange", type = "fanout"))
    })
    @RabbitHandler
    public void receive2(String message) {
        System.out.println("message2 = " + message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "directsExchange", type = "direct"),
                    key = {"directsKey1"})
    })
    @RabbitHandler
    public void receive3(String message) {
        System.out.println("message3 = " + message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "directsExchange", type = "direct"),
                    key = {"directsKey2"})
    })
    @RabbitHandler
    public void receive4(String message) {
        System.out.println("message4 = " + message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "directsExchange", type = "direct"),
                    key = {"directsKey1", "directsKey2"})
    })
    @RabbitHandler
    public void receive5(String message) {
        System.out.println("message5 = " + message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "topicExchange", type = "topic"),
                    key = {"topicKey.*"}) // *为匹配一个单词
    })
    @RabbitHandler
    public void receive6(String message) {
        System.out.println("message6 = " + message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "topicExchange", type = "topic"),
                    key = {"topicKey.#"}) // #为匹配多个单词
    })
    @RabbitHandler
    public void receive7(String message) {
        System.out.println("message7 = " + message);
    }
}
```

### 异步实践

将与业务无关的代码通过异步请求发送到队列中，监听器监听到后实现逻辑代码

1. 在`properties`定义队列、交换机、路由键名

   ```properties
   mq.env=local
   log.user.queue.name=${mq.env}.log.user.queue
   log.user.exchange.name=${mq.env}.log.user.exchange
   log.user.routing.key.name=${mq.env}.log.user.routing.key
   
   mail.queue.name=${mq.env}.mail.queue
   mail.exchange.name=${mq.env}.mail.exchange
   mail.routing.key.name=${mq.env}.mail.routing.key
   ```

2. 配置文件中创建上述三个

   ```java
   /**
    * 单一消费者配置
    *
    * @return
    */
   @Bean(name = "singleListenerContainer")
   public SimpleRabbitListenerContainerFactory listenerContainer() {
       SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
       factory.setConnectionFactory(connectionFactory);
       // factory.setMessageConverter(new Jackson2JsonMessageConverter());
       factory.setConcurrentConsumers(1);
       factory.setMaxConcurrentConsumers(1);
       factory.setPrefetchCount(1);
       factory.setTxSize(1);
       factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
       return factory;
   }
   
   @Bean
   public RabbitTemplate rabbitTemplate() {
       connectionFactory.setPublisherConfirms(true);
       connectionFactory.setPublisherReturns(true);
       RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
       rabbitTemplate.setMandatory(true);
       rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
           @Override
           public void confirm(CorrelationData correlationData, boolean ack, String cause) {
               log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
           }
       });
       rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
           @Override
           public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
               log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange, routingKey, replyCode, replyText, message);
           }
       });
       return rabbitTemplate;
   }
   
   /*************************** 异步 ***************************/
   @Bean
   public Queue logUserQueue() {
       return new Queue(environment.getProperty("log.user.queue.name"), true);
   }
   @Bean
   public DirectExchange logUserExchange() {
       return new DirectExchange(environment.getProperty("log.user.exchange.name"), true, false);
   }
   @Bean
   public Binding logUserBinding() {
       return BindingBuilder.bind(logUserQueue()).to(logUserExchange()).with(environment.getProperty("log.user.routing.key.name"));
   }
   @Bean
   public Queue mailQueue() {
       return new Queue(environment.getProperty("mail.queue.name"), true);
   }
   @Bean
   public DirectExchange mailExchange() {
       return new DirectExchange(environment.getProperty("mail.exchange.name"), true, false);
   }
   @Bean
   public Binding mailBinding() {
       return BindingBuilder.bind(mailQueue()).to(mailExchange()).with(environment.getProperty("mail.routing.key.name"));
   }
   /*************************** 削峰 ***************************/
   @Bean
   public Queue userOrderQueue() {
       return new Queue(environment.getProperty("user.order.queue.name"), true);
   }
   ```

3. 监听器

   ```java
   /*************************** 异步 ***************************/
   @RabbitListener(queues = "${log.user.queue.name}", containerFactory = "singleListenerContainer")
   public void logsQueue(@Payload byte[] message) throws IOException {
       log.info("log监听消费用户日志 监听到消息： {} ", message);
       Hello hello = objectMapper.readValue(message, Hello.class);
       log.info("log监听消费用户日志 监听到消息： {} ", hello);
       // TODO: 真正在这执行写日志操作
   }
   @RabbitListener(queues = "${mail.queue.name}", containerFactory = "singleListenerContainer")
   public void mailQueue(@Payload byte[] message) throws IOException {
       log.info("mail监听消费用户日志 监听到消息： {} ", message);
       Hello hello = objectMapper.readValue(message, Hello.class);
       log.info("mail监听消费用户日志 监听到消息： {} ", hello);
       // TODO: 真正在这执行发送邮件操作
   }
   ```

4. 生产者

   ```java
   /************************************************ rabbitmq异步实践 ************************************************/
   @ApiOperation(value = "实践异步记录用户操作日志", notes = "将与业务不相关的代码通过异步请求执行")
   @GetMapping("/logs")
   public void logs(Hello hello) throws JsonProcessingException {
       // TODO: 在这里执行其他逻辑操作
       // 异步写日志
       rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
       rabbitTemplate.setExchange(environment.getProperty("log.user.exchange.name"));
       rabbitTemplate.setRoutingKey(environment.getProperty("log.user.routing.key.name"));
       Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(hello)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
       message.getMessageProperties().setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, MessageProperties.CONTENT_TYPE_JSON);
       rabbitTemplate.convertAndSend(message);
   }
   @ApiOperation(value = "实践异步发送邮件", notes = "将与业务不相关的代码通过异步请求执行")
   @GetMapping("/mail")
   public void mail(Hello hello) throws JsonProcessingException {
       // TODO: 在这里执行其他逻辑操作
       // 异步发送邮件
       rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
       rabbitTemplate.setExchange(environment.getProperty("mail.exchange.name"));
       rabbitTemplate.setRoutingKey(environment.getProperty("mail.routing.key.name"));
       Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(hello)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
       message.getMessageProperties().setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, MessageProperties.CONTENT_TYPE_JSON);
       rabbitTemplate.convertAndSend(message);
   }
   ```

### 削峰实践

将巨大的请求发送到队列中而不是直接请求接口，减少数据库读写锁冲突的发生以及由于接口逻辑的复杂出现线程堵塞而导致应用占据服务器资源飙升

1. 在`properties`定义队列、交换机、路由键名

   ```properties
   mq.env=local
   user.order.queue.name=${mq.env}.user.order.queue
   user.order.exchange.name=${mq.env}.user.order.exchange
   user.order.routing.key.name=${mq.env}.user.order.routing.key
   ```

2. 配置文件中创建上述三个

   ```java
   /*************************** 削峰 ***************************/
   @Bean
   public Queue userOrderQueue() {
       return new Queue(environment.getProperty("user.order.queue.name"), true);
   }
   @Bean
   public TopicExchange userOrderExchange() {
       return new TopicExchange(environment.getProperty("user.order.exchange.name"), true, false);
   }
   @Bean
   public Binding userOrderBinding() {
       return BindingBuilder.bind(userOrderQueue()).to(userOrderExchange()).with(environment.getProperty("user.order.routing.key.name"));
   }
   @Bean
   public SimpleMessageListenerContainer listenerContainer(@Qualifier("userOrderQueue") Queue userOrderQueue) {
       SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
       container.setConnectionFactory(connectionFactory);
       MessageListenerAdapter adapter = new MessageListenerAdapter();
       adapter.setMessageConverter(new Jackson2JsonMessageConverter());
       container.setMessageListener(adapter);
       // 并发配置
       container.setConcurrentConsumers(environment.getProperty("spring.rabbitmq.listener.simple.concurrency", Integer.class));
       container.setMaxConcurrentConsumers(environment.getProperty("spring.rabbitmq.listener.simple.max-concurrency", Integer.class));
       container.setPrefetchCount(environment.getProperty("spring.rabbitmq.listener.simple.prefetch", Integer.class));
       /*
        * 消息确认
        * 对于某些消息而言，我们有时候需要严格的知道消息是否已经被 consumer 监听消费处理了，即我们有一种消息确认机制来保证我们的消息是否已经真正的被消费处理
        * 所以消息确认处理机制需要改成手动模式，需要自定义监听器实现 ChannelAwareMessageListener
        */
       container.setQueues(userOrderQueue); // 指定队列
       container.setMessageListener(userOrderListener); // 指定自定义监听器
       container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
       return container;
   }
   ```

3. 监听器实现`ChannelAwareMessageListener`

   ```java
   package com.caston.base_on_spring_boot.rabbitmq.listener;
   
   import com.caston.base_on_spring_boot.rabbitmq.service.RabbitService;
   import com.fasterxml.jackson.databind.ObjectMapper;
   import com.rabbitmq.client.Channel;
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;
   import org.springframework.amqp.core.Message;
   import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
   import org.springframework.stereotype.Component;
   
   import javax.annotation.Resource;
   
   @Component
   public class UserOrderListener implements ChannelAwareMessageListener {
       private static final Logger log = LoggerFactory.getLogger(UserOrderListener.class);
       @Resource
       private ObjectMapper objectMapper;
       @Resource
       private RabbitService rabbitService;
   
       @Override
       public void onMessage(Message message, Channel channel) throws Exception {
           long tag = message.getMessageProperties().getDeliveryTag();
           try {
               byte[] body = message.getBody();
               String phone = new String(body, "UTF-8");
               log.info("监听到抢单手机号：{}", phone);
               // TODO: 请求到这时去服务层处理业务逻辑
               rabbitService.manageNum(String.valueOf(phone));
               // 确认消费
               channel.basicAck(tag, true);
           } catch (Exception e) {
               log.error("用户抢单 发送异常：", e.fillInStackTrace());
               // 确认消费
               channel.basicReject(tag, false);
           }
       }
   }
   ```

4. 模拟高并发的生产者

   ```java
   /************************************************ rabbitmq削峰实践 ************************************************/
   private static final int ThreadNum = 5000;
   private static int phone = 0;
   @Resource
   private RabbitService rabbitService;
   /**
    * 将抢单请求的手机号信息压入队列，等待排队处理
    *
    * @param phone
    */
   public void sendRabbitMsg(String phone) {
       try {
           rabbitTemplate.setExchange(environment.getProperty("user.order.exchange.name"));
           rabbitTemplate.setRoutingKey(environment.getProperty("user.order.routing.key.name"));
           Message message = MessageBuilder.withBody(phone.getBytes("UTF-8")).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
           rabbitTemplate.send(message);
       } catch (Exception e) {
           log.error("发送抢单信息入队列 发送异常：phone={}", phone);
       }
   }
   /**
    * 使用CountDownLatch模拟高并发同时发送5000个请求
    */
   public void generateMultiThread() {
       log.info("开始初始化线程数-----> ");
       try {
           CountDownLatch countDownLatch = new CountDownLatch(1);
           for (int i = 0; i < ThreadNum; i++) {
               new Thread(new RunThread(countDownLatch)).start();
           }
           countDownLatch.countDown();
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
   private class RunThread implements Runnable {
       private final CountDownLatch startLatch;
       private RunThread(CountDownLatch startLatch) {
           this.startLatch = startLatch;
       }
       @Override
       public void run() {
           try {
               startLatch.await();
               phone += 1;
               sendRabbitMsg(String.valueOf(phone)); // 发送消息到队列中
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
   }
   @ApiOperation(value = "实践高并发下的队列请求", notes = "将请求发送到队列中")
   @GetMapping("/userOrder")
   public void userOrder() {
       generateMultiThread();
   }
   ```

### 死信队列实践

延迟队列，消息先到死信队列后，保存一段时间，一段时间后再发送到死信交换机绑定的队列中

1. 在`properties`定义（死信、消费者）队列、交换机、路由键名

   ```properties
   mq.env=local
   user.order.dead.queue.name=${mq.env}.user.order.dead.queue
   user.order.dead.exchange.name=${mq.env}.user.order.dead.exchange
   user.order.dead.routing.key.name=${mq.env}.user.order.dead.routing.key
   user.order.dead.real.queue.name=${mq.env}.user.order.dead.real.queue
   user.order.dead.produce.exchange.name=${mq.env}.user.order.dead.produce.exchange
   user.order.dead.produce.routing.key.name=${mq.env}.user.order.dead.produce.routing.key
   ```

2. 配置文件中创建上述六个

   ```java
   /**
    * 多个消费者配置
    *
    * @return
    */
   @Bean(name = "multiListenerContainer")
   public SimpleRabbitListenerContainerFactory multiListenerContainer() {
       SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
       factoryConfigurer.configure(factory, connectionFactory);
       factory.setMessageConverter(new Jackson2JsonMessageConverter());
       factory.setAcknowledgeMode(AcknowledgeMode.NONE);
       factory.setConcurrentConsumers(environment.getProperty("spring.rabbitmq.listener.simple.concurrency", int.class));
       factory.setMaxConcurrentConsumers(environment.getProperty("spring.rabbitmq.listener.simple.max-concurrency", int.class));
       factory.setPrefetchCount(environment.getProperty("spring.rabbitmq.listener.simple.prefetch", int.class));
       return factory;
   }
   @Bean
   public RabbitTemplate rabbitTemplate() {
       connectionFactory.setPublisherConfirms(true);
       connectionFactory.setPublisherReturns(true);
       RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
       rabbitTemplate.setMandatory(true);
       rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
           @Override
           public void confirm(CorrelationData correlationData, boolean ack, String cause) {
               log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
           }
       });
       rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
           @Override
           public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
               log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange, routingKey, replyCode, replyText, message);
           }
       });
       return rabbitTemplate;
   }
   /*************************** 死信队列（延迟队列） ***************************/
   @Bean
   public Queue userOrderDeadQueue() {
       Map<String, Object> args = new HashMap<>();
       args.put("x-dead-letter-exchange", environment.getProperty("user.order.dead.exchange.name"));
       args.put("x-dead-letter-routing-key", environment.getProperty("user.order.dead.routing.key.name"));
       args.put("x-message-ttl", 10000);
       return new Queue(environment.getProperty("user.order.dead.queue.name"), true, false, false, args);
   }
   @Bean
   public TopicExchange userOrderDeadExchange() {
       return new TopicExchange(environment.getProperty("user.order.dead.produce.exchange.name"), true, false);
   }
   @Bean
   public Binding userOrderDeadBinding() {
       return BindingBuilder.bind(userOrderDeadQueue()).to(userOrderDeadExchange()).with(environment.getProperty("user.order.dead.produce.routing.key.name"));
   }
   @Bean
   public Queue userOrderDeadRealQueue() {
       return new Queue(environment.getProperty("user.order.dead.real.queue.name"), true);
   }
   @Bean
   public TopicExchange userOrderDeadRealExchange() {
       return new TopicExchange(environment.getProperty("user.order.dead.exchange.name"));
   }
   @Bean
   public Binding userOrderDeadRealBinding() {
       return BindingBuilder.bind(userOrderDeadRealQueue()).to(userOrderDeadRealExchange()).with(environment.getProperty("user.order.dead.routing.key.name"));
   }
   ```

3. 监听器

   ```java
   /*************************** 死信队列（延迟队列） ***************************/
   @RabbitListener(queues = "${user.order.dead.real.queue.name}", containerFactory = "multiListenerContainer")
   public void consumeMessage(@Payload Integer id) {
       try {
           log.info("死信队列-用户超时监听信息：{}", id);
           if ((Integer) RabbitController.MAP.get("status") == 1) {
               RabbitController.MAP.replace("status", 3);
               log.info("这里为未支付模拟情况");
           } else {
               // TODO: 其他逻辑操作
               log.info("这里为已支付模拟情况");
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
   ```

4. 生产者

   ```java
   /************************************************ rabbitmq死信队列实践 ************************************************/
   public static final Map<String, Object> MAP = new HashMap<>(3);
   @PostMapping("/deadQueue")
   public void pushUserOrder() {
       MAP.put("id", 10);
       MAP.put("status", 1);
       rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
       rabbitTemplate.setExchange(environment.getProperty("user.order.dead.produce.exchange.name"));
       rabbitTemplate.setRoutingKey(environment.getProperty("user.order.dead.produce.routing.key.name"));
       rabbitTemplate.convertAndSend(10, new MessagePostProcessor() {
           @Override
           public Message postProcessMessage(Message message) throws AmqpException {
               MessageProperties properties = message.getMessageProperties();
               properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
               properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, Integer.class);
               return message;
           }
       });
   }
   ```

## Elasticsearch

### Mysql和Elasticsearch对比

Elasticsearch是面向文档，一切都是Json，elasticsearch使用的是倒排索引，适用于快速的全文搜索。通过value去查找key。

| Mysql | Elasticsearch |
| :---: | :-----------: |
|  表   |     index     |
|  行   |   documents   |
| 字段  |    fields     |

### 引用依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

### `yml`文件配置

```yaml
spring:
	elasticsearch:
  		rest:
    		uris:
      			- localhost
```

### 配置文件链接

```java
package com.caston.base_on_spring_boot.elasticsearch.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {
    @Value("${spring.elasticsearch.rest.uris}")
    private String uris;

    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration configuration = ClientConfiguration.builder().connectedTo(uris).build();
        return RestClients.create(configuration).rest();
    }
}
```

### 简单使用

- 实体类中使用注解定义其中的`document`和`field`

  ```java
  package com.caston.base_on_spring_boot.elasticsearch.model;
  
  import lombok.AllArgsConstructor;
  import lombok.Data;
  import lombok.NoArgsConstructor;
  import org.springframework.data.annotation.Id;
  import org.springframework.data.elasticsearch.annotations.Document;
  import org.springframework.data.elasticsearch.annotations.Field;
  import org.springframework.data.elasticsearch.annotations.FieldType;
  
  import java.io.Serializable;
  import java.util.List;
  import java.util.Map;
  
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Document(indexName = "elasticsearch")
  public class Elasticsearch implements Serializable {
      @Id
      private Integer id;
  
      @Field(type = FieldType.Keyword)
      private Long num;
  
      @Field(type = FieldType.Integer)
      private Integer age;
  
      @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_max_word")
      private String desc;
  
      @Field(type = FieldType.Keyword, analyzer = "ik_smart", searchAnalyzer = "ik_max_word")
      private String name;
  
      private Map<String, List<String>> highlights;
  }
  ```

- 定义一个继承`ElasticsearchRepository`的类

- 使用`ElasticsearchRestTemplate`或者继承了`ElasticsearchRepository`的类来操作`Elasticsearch`，`ElasticsearchRestTemplate`更多是看作`ElasticsearchRepository`的补充

## Shiro

### 引用依赖

```xml
<dependency>
	<groupId>org.apache.shiro</groupId>
	<artifactId>shiro-spring</artifactId>
	<version>1.7.1</version>
</dependency>
```

### 简单使用步骤

1. 编写realm，继承`AuthorizingRealm`

   - `doGetAuthorizationInfo`：进行授权的方法
   - `doGetAuthenticationInfo`：进行认证的方法

   ```java
   package com.caston.base_on_spring_boot.shiro.realm;
   
   import com.caston.base_on_spring_boot.shiro.service.AccountService;
   import org.apache.shiro.SecurityUtils;
   import org.apache.shiro.authc.*;
   import org.apache.shiro.authz.AuthorizationInfo;
   import org.apache.shiro.authz.SimpleAuthorizationInfo;
   import org.apache.shiro.realm.AuthorizingRealm;
   import org.apache.shiro.subject.PrincipalCollection;
   import org.apache.shiro.subject.Subject;
   import org.apache.shiro.util.ByteSource;
   import org.springframework.beans.factory.annotation.Autowired;
   import com.caston.base_on_spring_boot.shiro.entity.Account;
   
   import java.util.Arrays;
   import java.util.Set;
   import java.util.stream.Collectors;
   
   public class AccountRealm extends AuthorizingRealm {
       @Autowired
       private AccountService accountService;
   
       /**
        * 授权
        *
        * @param principalCollection
        * @return
        */
       @Override
       protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
           // 获取当前登录的用户信息
           Subject subject = SecurityUtils.getSubject();
           Account account = (Account) subject.getPrincipal();
           String[] roles = account.getRole().split(",");
           // 设置角色
           Set<String> rolesSet = Arrays.stream(roles).collect(Collectors.toSet());
           SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(rolesSet);
           String[] perms = account.getPerms().split(",");
           Set<String> permsSet = Arrays.stream(perms).collect(Collectors.toSet());
           // 设置权限
           info.addStringPermissions(permsSet);
           return info;
       }
   
       /**
        * 认证
        *
        * @param authenticationToken
        * @return
        * @throws AuthenticationException
        */
       @Override
       protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
           UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
           Account account = accountService.lambdaQuery().eq(Account::getUsername, token.getUsername()).one();
           if (account != null) {
               /* 参数一：通过用户名查询到的用户信息
                * 参数二：用户的密码
                * 参数三：密码的加密策略，bjsxd为自定义的盐
                * 参数四：realm名
                */
               return new SimpleAuthenticationInfo(account, account.getPassword(), ByteSource.Util.bytes("bjsxd"), getName());
           }
           return null;
       }
   }
   ```

2. 编写配置类

   ```java
   package com.caston.base_on_spring_boot.shiro.config;
   
   import com.caston.base_on_spring_boot.shiro.filter.ShiroFilter;
   import com.caston.base_on_spring_boot.shiro.realm.AccountRealm;
   import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
   import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
   import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
   import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
   import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.beans.factory.annotation.Qualifier;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   
   import javax.servlet.Filter;
   import java.util.HashMap;
   import java.util.LinkedHashMap;
   
   @Configuration
   public class ShiroConfig {
   
       @Bean
       public AccountRealm accountRealm() {
           // 注入realm
           return new AccountRealm();
       }
   
       @Bean
       public DefaultWebSecurityManager securityManager(@Qualifier("accountRealm") AccountRealm accountRealm) {
           DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
           // 创建MD5加密对象
           HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
           matcher.setHashAlgorithmName("md5");
           matcher.setHashIterations(2);
           // 将加密对象设置进realm中
           accountRealm.setCredentialsMatcher(matcher);
           // 将自定义realm放入manager
           manager.setRealm(accountRealm);
           return manager;
       }
   
       @Bean
       public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
           ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
           // 将前面的manager放入工厂中
           factoryBean.setSecurityManager(defaultWebSecurityManager);
           // 自定义权限过滤器
           LinkedHashMap<String, Filter> mapFilter = new LinkedHashMap<>();
           mapFilter.put("rolesOr", new ShiroFilter());
           factoryBean.setFilters(mapFilter);
           HashMap<String, String> map = new HashMap<>();
           map.put("/account/login/**", "anon");// 不用认证就能进入
           map.put("/swagger-ui/**", "anon");
           map.put("/swagger-resources/**", "anon");
           map.put("/v3/**", "anon");
           map.put("/webjars/**", "anon");
           map.put("/account/test/**", "rolesOr[manager,user]");// 使用上面自定义的过滤器
           map.put("/**", "authc");// 需要认证才能进入
           //  map.put("/account/perms/**","perms[manager]");// 配置什么权限能访问的
           //  map.put("/account/roles/**","roles[admin]");// 配置什么角色能访问的
           factoryBean.setFilterChainDefinitionMap(map);
           // 设置登录页面
           factoryBean.setLoginUrl("/account/reLogin");
           // 设置未授权页面
           factoryBean.setUnauthorizedUrl("/account/unauthc");
           return factoryBean;
       }
   
       /**
        * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions)
        * 配置以下两个bean(DefaultAdvisorAutoProxyCreator和AuthorizationAttributeSourceAdvisor)即可实现此功能
        *
        * @return
        */
       @Bean
       public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
           DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
           advisorAutoProxyCreator.setProxyTargetClass(true);
           return advisorAutoProxyCreator;
       }
   
       @Bean
       public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
           AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
           authorizationAttributeSourceAdvisor.setSecurityManager(defaultWebSecurityManager);
           return authorizationAttributeSourceAdvisor;
       }
   }
   ```

3. 认证使用

   - 将用户输入的名和密码封装进`UsernamePasswordToken`中，进行`subject.login`时会进入realm中的认证方法

   ```java
   	@PostMapping("/login")
       public String login(String username, String password) {
           Subject subject = SecurityUtils.getSubject();
           UsernamePasswordToken token = new UsernamePasswordToken(username, password);
           try {
               subject.login(token);
               Account account = (Account) subject.getPrincipal();
               subject.getSession().setAttribute("account", account);
           } catch (Exception e) {
               e.printStackTrace();
               return "登录失败";
           }
           return "登录成功";
       }
   ```

4. 授权使用

   - 使用配置类配置

   - 使用注解（当权限发生重叠时，注解会覆盖配置类）

     ```java
     	@RequiresPermissions(value = {"manager:all", "user:select"}, logical = Logical.OR)
         @GetMapping("/perms")
         public String perms() {
             return "perms";
         }
     
         @RequiresRoles(value = "user")
         @GetMapping("/roles")
         public String roles() {
             return "roles";
         }
     ```

### 拓展

#### 自定义过滤规则

继承`AuthorizationFilter`，编写后在配置类配置并使用

```java
package com.caston.base_on_spring_boot.shiro.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ShiroFilter extends AuthorizationFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        Subject subject = getSubject(servletRequest, servletResponse);
        String[] rolesArray = (String[]) o;
        //没有角色限制，没有权限访问
        if (rolesArray == null || rolesArray.length == 0) {
            return false;
        }
        for (String role : rolesArray) {
            if (subject.hasRole(role)) {
                // true为放行
                return true;
            }
        }
        return false;
    }
}
```

#### 使用Shiro进行MD5加密

```java
String password = "123123";
Md5Hash md5Hash = new Md5Hash(password);
System.out.println(md5Hash.toHex());
// 使用加盐后的加密数据
Md5Hash md5Hash1 = new Md5Hash(password, "bjsxd");
System.out.println(md5Hash1.toHex());
// 使用迭代并加盐后的加密数据
Md5Hash md5Hash2 = new Md5Hash(password, "bjsxd", 2);
System.out.println(md5Hash2.toHex());
```

# Java基础

1. 红黑树
   - 性质1：每个节点要么是黑色，要么是红色。
   - 性质2：根节点是黑色。
   - 性质3：每个叶子节点（NIL）是黑色。
   - 性质4：每个红色结点的两个子结点一定都是黑色。
   - 性质5：任意一结点到每个叶子结点的路径都包含数量相同的黑结点。
2. `ThreadLocal`
   - `ThreadLocal`用来存储变量副本，键值为当前`ThreadLocal`变量，value为变量副本（即T类型的变量）
3. `JVM`内容存放
   - 程序计数器：存放下一条将要执行的指令的地址
   - 堆：运行时动态申请的内存都在堆上分配，包括new的对象和数组；静态成员变量、常量池等也在堆中
   - 虚拟机栈：存放局部变量、对象引用、操作数栈、方法出口等；后进先出，被调方法结束后，对应栈区变量等立即销毁
   - 本地方法栈：主要与虚拟机用到native方法有关
   - 元空间：存储的是类的元信息
4. Array List 、Linked List
   - Array List已经分配好内存地址，所以查询快
   - Linked List额外实现了Deque接口，所以可以当作双端队列来使用
   - Array List添加时会涉及扩容，默认容量为10，不足时按1.5倍增长；当给指定位置添加元素时，还会涉及元素的移动
   - Linked List链表不存在扩容的概念；当给指定位置添加元素时，会先遍历数组找出对应位置再添加
   - Array 可以包含基本类型和对象类型，Array List 只能包含对象类型。
   - Array 大小是固定的，Array List 的大小是动态变化的。
5. Hash Map
   - Hash Map采用数组+链表+红黑树
   - 默认容量16，负载因子0.75，默认阈值12
   - 当前存入数据大于阈值即发生扩容
   - 链表个数大于8时，且哈希桶数组长度大于64的时候，链表转换为红黑树，如果大于8数组长度小于64时优先进行扩容，变成红黑树的目的是提高搜索速度，高效查询
   - 当哈希表实际节点数达到容量的75%的时候需要调用resize方法进行扩容；Hash Map的默认容量时16，负载因子为0.75。当Hash Map储存程度达到容量的75%时，将会创建原来大小两倍的数组，并将数组重新hash放入新的bucket中，所以频繁的扩容会消耗性能
   - 初始化时，容量为0，当第一次添加元素时扩容后为16
   - 初始化时是先扩容再插入值；之后是先插入值再扩容
6. Hash Map 的长度为什么是2的幂次方
   - 取余(%)操作中如果除数是2的幂次则等价于与其除数减一的与(&)操作（也就是说 hash%length==hash&(length-1)的前提是 length 是2的 n 次方；）。并且 采用二进制位操作 &，相对于%能够提高运算效率，这就解释了 Hash Map 的长度为什么是2的幂次方。
7. Map集合类

   - `LinkedHashMap`在遍历的时候会比 `HashMap`慢，不过有种情况例外，当 `HashMap`容量很大，实际数据较少时，遍历起来可能会比 `LinkedHashMap`慢，因为 `LinkedHashMap` 的遍历速度只和实际数据有关，和容量无关，而 `HashMap` 的遍历速度和他的容量有关。当用 Iterator 遍历 `TreeMap` 时，得到的记录是排过序的。
8. String 和String Buffer的区别
   - 它们可以储存和操作字符串，即包含多个字符的字符数据。这个String类提供了数值不可改变的字符串。而这个String Buffer类提供的字符串进行修改。当你知道字符数据要改变的时候你就可以使用String Buffer。典型地，你可以使用String Buffer来动态构造字符数据。
9. 说出Array List,Vector, Linked List的存储性能和特性
   - Array List和Vector都是使用数组方式存储数据，此数组元素数大于实际存储的数据以便增加和插入元素，它们都允许直接按序号索引元素，但是插入元素要涉及数组元素移动等内存操作，所以索引数据快而插入数据慢，Vector由于使用了synchronized方法（线程安全），通常性能上较Array List差，而Linked List使用双向链表实现存储，按序号索引数据需要进行前向或后向遍历，但是插入数据时只需要记录本项的前后项即可，所以插入速度较快。
10. `Servlet`的生命周期
    - `Servlet`被服务器实例化后，容器运行其`init`方法，请求到达时运行其service方法，service方法自动派遣运行与请求对应的`doXXX`方法（`doGet`，`doPost`）等，当服务器决定将实例销毁的时候调用其destroy方法。
11. Collection 和 Collections的区别
    - Collection是集合类的上级接口，继承与他的接口主要有Set 和List.
    - Collections是针对集合类的一个帮助类，他提供一系列静态方法实现对各种集合的搜索、排序、线程安全化等操作。
12. Hash Map和Hash table的区别
    - Hash Map是Hash table的轻量级实现（非线程安全的实现），他们都完成了Map接口，主要区别在于Hash Map允许空（null）键值（key）,由于非线程安全，效率上可能高于Hash table。
    - Hash Map允许将null作为一个entry的key或者value，而Hash table不允许。
    - 最大的不同是，Hash table的方法是Synchronize的，而Hash Map不是，在多个线程访问Hash table时，不需要自己为它的方法实现同步，而Hash Map 就必须为之提供外同步**（如果是Array List:` List lst = Collections.synchronizedList(new ArrayList());`如果是Hash Map: `Map map = Collections.synchronizedMap(new HashMap());`）**
13. sleep() 和 wait() 有什么区别
    - sleep是线程类（Thread）的方法，导致此线程暂停执行指定时间，给执行机会给其他线程，但是监控状态依然保持，到时后会自动恢复。调用sleep不会释放对象锁。
    - wait是Object类的方法，对此对象调用wait方法导致本线程放弃对象锁，进入等待此对象的等待锁定池，只有针对此对象发出notify方法（或`notifyAll`）后本线程才进入对象锁定池准备获得对象锁进入运行状态。
14. forward 和redirect的区别
    - forward是服务器请求资源，服务器直接访问目标地址的URL，把那个URL的响应内容读取过来，然后把这些内容再发给浏览器，浏览器根本不知道服务器发送的内容是从哪儿来的，所以它的地址栏中还是原来的地址。
    - redirect就是服务端根据逻辑,发送一个状态码,告诉浏览器重新去请求那个地址，一般来说浏览器会用刚才请求的所有参数重新请求，所以session,request参数都可以获取。
15. `short s1 = 1; s1 = s1 + 1;`有什么错? `short s1 = 1; s1 += 1;`有什么错?
    - `short s1 = 1; s1 = s1 + 1; `（`s1+1`运算结果是`int`型，需要强制转换类型）
    - `short s1 = 1; s1 += 1;`（可以正确编译）
16. `Math.round(11.5)`等於多少? `Math.round(-11.5)`等於多少
    - `Math.round(11.5)==12`
    - `Math.round(-11.5)==-11`
    - round方法返回与参数最接近的长整数，参数加1/2后求其floor.
17. 常见到的runtime exception
    - `ArithmeticException, ArrayStoreException, BufferOverflowException, BufferUnderflowException, CannotRedoException, CannotUndoException, ClassCastException, CMMException, ConcurrentModificationException, DOMException, EmptyStackException, IllegalArgumentException, IllegalMonitorStateException, IllegalPathStateException, IllegalStateException, ImagingOpException, IndexOutOfBoundsException, MissingResourceException, NegativeArraySizeException, NoSuchElementException, NullPointerException, ProfileDataException, ProviderException, RasterFormatException, SecurityException, SystemException, UndeclaredThrowableException, UnmodifiableSetException, UnsupportedOperationException`
18. 接口是否可继承接口? 抽象类是否可实现(implements)接口? 抽象类是否可继承实体类(concrete class)
    - 接口可以继承接口。抽象类可以实现(implements)接口，抽象类是否可继承实体类，但前提是实体类必须有明确的构造函数。
19. 数据连接池的工作机制是什么
    - `J2EE`服务器启动时会建立一定数量的池连接，并一直维持不少于此数目的池连接。客户端程序需要连接时，池驱动程序会返回一个未使用的池连接并将其表记为忙。如果当前没有空闲连接，池驱动程序就新建一定数量的连接，新建连接的数量有配置参数决定。当使用的池连接调用完成后，池驱动程序将此连接表记为空闲，其他调用就可以使用这个连接。
20. 是否可以继承String类
    - String类是final类故不可以继承。
21. `swtich`是否能作用在byte上，是否能作用在long上，是否能作用在String上
    - `switch（expr1）`中，`expr1`是一个整数表达式。因此传递给 switch 和 case 语句的参数应该是 `int`、 short、 char 或者 byte。long不能作用于`swtich`。
22. XML文档定义有几种形式？它们之间有何本质区别？解析XML文档有哪几种方式
    - 两种形式 `dtd schema`
    - 本质区别:schema本身是`xml`的，可以被XML解析器解析(这也是从`DTD`上发展schema的根本目的)
    - 有`DOM,SAX,STAX`等 
      - DOM:处理大型文件时其性能下降的非常厉害。这个问题是由DOM的树结构所造成的，这种结构占用的内存较多，而且DOM必须在解析文件之前把整个文档装入内存,适合对XML的随机访问
      - SAX:不现于DOM,SAX是事件驱动型的XML解析方式。它顺序读取XML文件，不需要一次全部装载整个文件。当遇到像文件开头，文档结束，或者标签开头与标签结束时，它会触发一个事件，用户通过在其回调事件中写入处理代码来处理XML文件，适合对XML的顺序访问 
      - `STAX:Streaming API for XML (StAX)`
23. 什么是Java序列化，如何实现Java序列化
    - 序列化就是一种用来处理对象流的机制，所谓对象流也就是将对象的内容进行流化。可以对流化后的对象进行读写操作，也可将流化后的对象传输于网络之间。序列化是为了解决在对对象流进行读写操作时所引发的问题。
    - 序列化的实现：将需要被序列化的类实现`Serializable`接口，该接口没有需要实现的方法，`implements Serializable`只是为了标注该对象是可被序列化的，然后使用一个输出流(如：`FileOutputStream`)来构造一个`ObjectOutputStream`(对象流)对象，接着，使用`ObjectOutputStream`对象的`writeObject`(`Object obj`)方法就可以将参数为`obj`的对象写出(即保存其状态)，要恢复的话则用输入流。
24. 写clone()方法时，通常都有一行代码，是什么
    - Clone 有缺省行为，`super.clone()`;他负责产生正确大小的空间，并逐位复制。
25. List、Map、Set三个接口，存取元素时，各有什么特点
    - List 以特定次序来持有元素，可有重复元素。Set 无法拥有重复元素,内部排序。Map 保存key-value值，value可多值
26. 数组集合互转
    - 数组转集合：`aslist`
    - 集合转数组：`toArray`
27. 字符串转时间格式

```java
String time="2010-11-20 11:10:10";
Date date=null;
SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
date=formatter.parse(time);
```

28. Java数据库链接池
    - `C3p0`: 实现数据源和`JNDI`绑定，支持`JDBC3`规范和`JDBC2`的标准扩展。Hibernate、Spring使用。单线程，性能较差，适用于小型系统，代码`600KB`左右。
    - `DBCP (Database Connection Pool)`：Apache的， commons-pool对象池机制，Tomcat使用。单独使用`dbcp`需要3个包：`common-dbcp.jar,common-pool.jar,common-collections.jar`，预先将数据库连接放内存中，建立数据库连接时，直接到连接池中申请，用完放回。单线程，并发量低，性能不好，适用于小型系统。
    - `Tomcat Jdbc Pool`：Tomcat在7.0以前都是使用，单线程，保证线程安全会锁整个连接池，性能差，超过60个类复杂。Tomcat从7.0开始叫做`Tomcat jdbc pool`，基于Tomcat JULI，使用Tomcat日志框架，完全兼容`dbcp`，异步方式获取连接，支持高并发应用环境，核心文件8个，支持`JMX`，支持`XA Connection`。
    - `BoneCP`：高效、免费。设计提高性能，速度最快，高度可扩展：集成Hibernate和`DataNucleus`中。连接状态切换的回调机制；允许直接访问连接；自动化重置能力；`JMX`支持；懒加载能力；支持XML和属性文件配置方式；较好的Java代码组织，100%单元测试分支代码覆盖率；代码`40KB`左右。
    - `Druid`：Java中最好，强大监控和扩展，可用于大数据实时查询和分析的高容错、高性能分布式系统，尤其是当发生代码部署、机器故障以及其他产品系统遇到宕机等情况时，100%正常运行。主要特色：分析监控；交互式查询快；高可用；可扩展；
29. object方法
    - `getClass`、`hashCode`、`equals`、`clone`、`toString`、`notify`、`notifyAll`、`wait`、`finalize`
30. `String`、`StringBuffer`、`StringBuilder`
    - `String`：字符串常量；`StringBuffer/StringBuilder`：字符串变量
31. 遍历Map

[网址](https://www.cnblogs.com/blest-future/p/4628871.html)

32. 线程创建的区别

[网址](https://www.cnblogs.com/htyj/p/10848646.html)

33. List中自定义对象怎么去重

[网址](https://blog.csdn.net/hjfcgt123/article/details/84781552)

34. `ArrayList`与`LinkList`哪个占用内存多

[网址](https://zhuanlan.zhihu.com/p/166686856)

35. Map是否有序

[网址](https://www.cnblogs.com/chen-lhx/p/8432422.html)

# `SSM`框架

## spring注入方式

- set注入
- 构造注入
- 通过注解方式注入（`@Autowride`）
- 自动装配（`byName`、`byType`）

## 接收前端过来的数据方式

[网址](https://www.cnblogs.com/mjs154/p/11667796.html)

## `resultMap`和`resultType`区别

[网址](https://mp.weixin.qq.com/s?src=11&timestamp=1614991627&ver=2929&signature=ZJsOBkFnEXPhuuYMeetOvzg18hW3L8A-vNzuzCTci-aZkpA-ag11750flZCkJhsfuJxR18-sYPKbcZkOpu1uxxbECGRaIKC3KpAHktHSqaoeHZYQ8zaNZKnfM*9yY12Q&new=1)

## `ioc`和`aop`理解

[网址](https://blog.csdn.net/qq_32534441/article/details/94889895)

## `orm`框架

`ORM` 是 Object Relational Mapping 的缩写，译为“对象关系映射”框架。

所谓的 `ORM` 框架就是一种为了解决面向对象与关系型数据库中数据类型不匹配的技术，它通过描述 Java 对象与数据库表之间的映射关系，自动将 Java 应用程序中的对象持久化到关系型数据库的表中。

`ORM`框架是一种数据持久化技术，即在对象模型和关系型数据库之间建立起对应关系，并且提供一种机制，可通过 `JavaBean` 对象操作数据库表中的数据

## SpringMVC流程

1. 客户请求被`DispatcherServlet`接收
2. `DispatcherServlet`根据`HandlerMapping`映射到`Handler`
3. 生成`Handler`和`HandlerInterceptor`
4. `Handler`和`HandlerInterceptor`以`HandlerExecutionChain`的形式一并返回给`DispatcherServlet`
5. `DispatcherServlet`通过`HandlerAdapter`调用`Handler`的方法
6. 返回一个`ModelAndView`对象给`DispatcherServlet`
7. 将获取到的`ModelAndView`传给视图解析器（`ViewResolve`），将逻辑视图解析成物理视图
8. 视图解析器返回一个View给`DispatcherServlet`
9. `DispatcherServlet`根据View进行渲染
10. 将渲染之后的视图响应给客户端

## Mybatis动态sql

`<if />、<choose />、<when />、<otherwise />、<trim />、<where />、<set />、<foreach />、<bind />`

## Spring中的bean

bean的作用域`scope`

- singleton，单例，表示通过Spring容器获取的对象是唯一的，该值默认值，在该模式下，只要加载IoC容器，无论是否取出bean，配置文件中的bean都会被创建
- prototype，原型，表示通过Spring容器获取的对象是不同的。在该模式下，如果不取bean，则不会创建对象，取一次bean就会创建一个对象
- request，请求，表示在一次HTTP请求内有效
- session，会话，表示在一个用户会话内有效

## Spring工厂方法

创建bean的两种方式

- 静态工厂方法
- 实力工厂方法

区别：静态工厂方法创建对象时不需要去实例化工厂对象

## `IoC`和`AOP`实现

`IoC`：结合反射机制动态动态创建对象并赋值，将创建好的bean存入Map集合，key就是bean中的id，value就是bean对象

`AOP`：使用代理模式加反射机制实现

- 把一些非业务逻辑的代码从业务中抽离出来，以非侵入的方式与原方法进行协同，这样可以使得原方法更专注于业务逻辑
- 日志场景、统计场景（方法调用次数，异常次数）、安防场景（熔断，限流和降级）、性能场景（缓存，超时控制）

# Spring Boot

- 定义全局异常注解：`@ControllerAdvice`
- 处理指定异常注解：`@ExceptionHandler`
- `@SpringBootApplication`包含

  - `@SpringBootConfiguration`
  - `@EnableAutoConfiguration`
  - `@ComponentScan`
- `@ImportResource`：可以引入一个XML配置

## Spring boot默认数据源
主要是内存数据库：`Tomcat、Hikari、Dbcp、Dbcp2`

  - Spring boot 1.0默认使用`Tomcat`
  - Spring boot 2.0默认使用`Hikari`

## Spring boot启动器

运行一些特定的代码需要实现接口`ApplicationRunner`或者`CommandLineRunner`，然后重写`run`方法

### 启动顺序

启动有多个`ApplicationRunner`和`CommandLineRunner`时，想控制启动顺序可以实现`org.springframework.core.Ordered`或者使用`org.springframework.core.annotation.Order`注解

##  Spring Boot读取配置相关注解

- `@PropertySource`
- `@Value`
- `@Environment`
- `@ConfifigurationProperties`

## Spring Boot自动装配原理

在`@SpringBootApplication`中的`@EnableAutoConfiguration`中的`@Import({AutoConfigurationImportSelector.class})`

首先去读取`autoconfigure.jar`下的`spring.factories`，将`autoconfigure`的值封装进一个list中，再去重排序，根据`spring-autoconfigure-metadata.properties`过滤，最后通过反射实例化对象并放入容器中管理

[相关链接](https://www.jianshu.com/p/88eafeb3f351?u_atoken=fa6877b9-f73f-4a28-98e8-acde99f2e32d&u_asession=01fWXKeWXipjBLrNwd5_cWWRuk6-XgTTF8ehGBhCYxPa_4OsryH0VsQDziJMZXJl1LX0KNBwm7Lovlpxjd_P_q4JsKWYrT3W_NKPr8w6oU7K_YCevXIk-RmChhVbhsaCFOzdjoMV1y19BFQvaXcOyBfmBkFo3NEHBv0PZUm6pbxQU&u_asig=05ZKWgk1Uml8jErQXZk-a32NdjI_eA2ijcDNkWf5xF07ppJVbedkbiL2ktfaxSPJix3-gg4ZkChELFNtwzItFXb4BMSfk33kOjWksP4cnRSPA-03k4lqRUFlXGCETBzZZ052r8SwUOSISuWDC7tz4dAALNEcnVEvOgXdh_jOWwV5n9JS7q8ZD7Xtz2Ly-b0kmuyAKRFSVJkkdwVUnyHAIJzTzV1zpIh405-Dffu88FKXBuiUTPgt-8ajpanQCW5Xq5qBR97QLsOYcZJeUxi-_JXu3h9VXwMyh6PgyDIVSG1W8eja8jWmzEZpJpI62cLJdFvcob-3mcfVCNS-ach8kgLgxFFdizMIklqFktSn363qdtWukvFcVQRgOfbIfHosEymWspDxyAEEo4kbsryBKb9Q&u_aref=QEYsosONoFPaHFaFjWf3hdY6MKQ%3D)

# 项目

## `Zxing`源码解读

[网址](https://blog.csdn.net/liangyihuai/article/details/72779135)

## `MD5`源码解读

[网址](https://www.cnblogs.com/android-blogs/p/5305598.html) [网址](https://www.jianshu.com/p/b419163272c1) 

## `JWT`

- 请求头
  - 令牌的类型`type`
  - 签名使用的算法`alg`
- 载荷
  - `sub`：主题
  - `iss`：签发者
  - `aud`：接收方
  - `iat`：签发时间
  - `exp`：过期时间
  - `nbf`：定义生效时间
  - `jti`：`jwt`的唯一身份标识，主要用来作为一次性token，从而避免重放攻击
- 签名

三部分使用 “.” 来分隔

# `MySQL`

## InnoDB、MyISAM

- MyISAM 只有表级锁(table-level locking)，而InnoDB 支持行级锁(row-level
  locking)和表级锁,默认为行级锁。
-  MyISAM 强调的是性能，每次查询具有原子性,其执行速度比
    InnoDB类型更快，但是不提供事务支持。但是InnoDB 提供事务支持事务，外部键等高级数据库
    功能。 具有事务(commit)、回滚(rollback)和崩溃修复能力(crash recovery capabilities)的事务安
    全(transaction-safe (ACID compliant))型表。
-  MyISAM不支持外键，而InnoDB支持。
-  是否支持MVCC ：仅 InnoDB 支持。应对高并发事务, MVCC比单纯的加锁更高效;MVCC只在 READ
    COMMITTED 和 REPEATABLE READ 两个隔离级别下工作;MVCC可以使用 乐观(optimistic)锁 和 悲
    观(pessimistic)锁来实现;各数据库中MVCC实现并不统一。

## `SQL`优化

[网址](https://zhuanlan.zhihu.com/p/126247848) [网址](https://www.cnblogs.com/wangqingming/p/9656999.html)

## 零散

`distinct`：Mysql去重

`select distinct 字段 from 表`

左连接：`select * from r left join s on r.a = s.a`

可重复读隔离级别可以解决读的幻读，没有解决写的幻读，数据库默认为可重复读隔离级别

[隔离级别](https://juejin.cn/post/6844903808376504327#heading-7)

[B+树](https://juejin.cn/post/6929833495082565646)

# `Redis`

缓存雪崩：同一时刻大量热点数据过期，大量请求直接通过数据库

- 解决方案：设置随机过期时间

缓存击穿：某一个热点数据过期，大量请求直接通过数据库

- 解决方案：设置热点数据不过期

缓存穿透：访问大量不存在的key，给数据造成压力

## 常用五种类型

String、List、Set、Sort Set、Hash

## `Redis`淘汰策略

[网址](https://zhuanlan.zhihu.com/p/105587132)

- `noeviction`：当内存使用超过配置时返回，不会驱逐任何键
- `allkey-lru`：加入键过限时，通过`lru`算法驱逐最久没有使用的键
- `volatile-lru`：加入键过限时，从设置了过期时间的键驱逐最久没有使用的键
- `allkeys-random`：加入键过限时，从所有key随机删除
- `volatile-random`：加入键过限时，从设置了过期时间的key中随机驱逐
- `volatile-ttl`：从设置了过期时间的key中驱逐马上就要过期的键
- `volatile-lfu`：从设置了过期时间的键中驱逐使用频率最少的键
- `allkeys-lfu`：从所有键中驱逐使用频率最少的键

## `Redis`持久化

- `AOF`：将每一条写操作持久化，生成`.aof`文件
- `RDB`：将当前数据持久化保存到硬盘，生成后缀名为`.rdb`文件，实际操作是fork一个子进程，先将数据集写入一个临时文件，写入成功后替换之前的文件，用二进制压缩存储

`AOF`默认没有开启，当两者开启时优先使用`AOF`来还愿数据

## `MySQL`和`Redis`区别

- `MySQL`是关系型数据库，主要用于持久化数据，将数据存储在硬盘中，每次访问数据库时，存在I/O操作，读取速度较慢
- `Redis`是非关系型数据库，将数据存储在内存、缓存中，缓存的读取速度快，大大提高运行效率
- `Redis`可用于缓存，也可用于分布式锁

## 缓存击穿、缓存穿透、缓存雪崩

### **缓存穿透**

指查询一个一定不存在的数据，由于缓存是不命中时需要从数据库查询，查不到数据则不写入缓存，这将导致这个不存在的数据每次请求都要到数据库去查询，进而给数据库带来压力

- 对非法请求进行过滤
- 如果查询数据库为空，给缓存设置空值，但是如有有写请求进来的话，需要更新缓存哈，以保证缓存一致性，同时，最后给缓存设置适当的过期时间。
- 使用布隆过滤器判断数据是否存在

### 缓存雪崩

指缓存中数据大批量到过期时间，而查询数据量巨大，请求都直接访问数据库，引起数据库压力过大甚至down机

- 设置随机过期时间

### 缓存击穿

指热点key在某个时间点过期的时候，而恰好在这个时间点对这个Key有大量的并发请求过来，从而大量的请求打到db。

- 使用互斥锁方案。缓存失效时，不是立即去加载db数据，而是先使用某些带成功返回的原子操作命令，如(Redis的setnx）去操作，成功的时候，再去加载db数据库数据和设置缓存。否则就去重试获取缓存。
- 设置永不过期

[链接](https://juejin.cn/post/7002011542145204261#heading-65)

------

# Java并发

## 线程基础

进程是操作系统进行资源分配的基本单位，线程是操作系统调度的基本单位

### 线程状态

初始`NEW`、运行`RUNNABLE`（就绪`READY` 运行中`RUNNING`）、阻塞`BLOCKED`、等待`WAITING`、超时等待`TIMED_WAITING`、终止`TERMINATED`

### 线程优先级

- 线程优先级范围是1~10，默认优先级为5
- 线程优先级具有继承性
  - 线程1启动线程2，线程1和2具有相同的优先级
- 即使优先级高，也不能保证一定获取CPU执行权

### 线程常用方法

- `yield()`：使当前线程让出CPU使用权
- `sleep()、yield()`不会释放对象监视器（锁），`wait()`会释放
- `wait()、notify()、notifyAll()`必须在`synchronized`语句块中执行

### `volatile`

- 可以保证可见性，但不可以保证原子性
- 可以禁止指令重排序

### `synchronized`锁对象

- 方法
  - 实例方法：类的实例对象
  - 静态方法：类的class对象
- 代码块：锁括号里面的东西

`synchronized`就是基于进入和退出Monitor对象来实现方法同步/代码块同步和释放的，线程执行`monitorenter`尝试获取对象监视器的所有权，`monitorexit`尝试释放对象监视器的所有权。当线程尝试获取锁执行同步代码时，线程会进入`_EntryList`区，通过`wait()`方法进入`_WaitSet`区，当线程释放Monitor后，`_EntryList`和`_WaitSet`等待的线程都会尝试竞争Monitor。

![本地](.\images\1.jpg)
![网络](https://gitee.com/mulberry_devil/gitee-pages-imgs/raw/master/2022-07-12_141052.jpg)

### 偏向锁、轻量级锁、重量级锁

- 锁只能升级，不能降级
- 单线程下，偏向锁消耗性能最低，出现其他线程竞争，偏向锁就会升级为轻量级锁，如果其他线程通过一定次数的自旋操作无法获取锁，轻量级锁就会升级为重量级锁

![本地](.\images\2.jpg)
![网络](https://gitee.com/mulberry_devil/gitee-pages-imgs/raw/master/2022-07-12_141101.jpg)

![本地](.\images\3.jpg)
![网络](https://gitee.com/mulberry_devil/gitee-pages-imgs/raw/master/2022-07-12_141103.jpg)

# 并发编程工具

## `CAS`

- 内存值V
- 旧的期望值A
- 将要修改的新值B

当且仅当A和V相等时，将V修改为B

## `AQS（AbstractQueuedSynchronizer）`

底层使用模板方法设计模式

### 独占模式

一个线程占有同步资源后，其余线程均不可以使用此同步资源

- 获取资源主要是通过`acquire()`方法，释放资源主要是通过`release()`
- `acquire()`
  - 通过`tryAcquire()`尝试获取同步资源，返回true时`acquire()`结束
  - 如果失败，执行`addWaiter()`将线程以节点的方法添加到队列的末尾，添加失败时会采取自旋的方式入队尾
  - 入队尾成功后，通过`acquireQueued()`中的`tryAcquire()`方法尝试在队列中获取资源，如果失败则调用`shouldParkAfterFailedAcquire()`方法判断是否阻塞线程
  - 不需要阻塞时`acquireQueued()`进入自旋继续尝试获取资源
  - 需要阻塞时执行`parkAndCheckInterrupt()`阻塞线程，等待前驱结点调用`unpark()`方法或者线程中断唤醒阻塞的线程
  - 当线程恢复执行后，判断线程是否中断并维护线程中断标识
- `release()`
  - 通过`tryRealease()`尝试获取释放同步资源的使用权限，返回false时释放失败
  - 执行`unparkSuccessor()`方法，该方法为唤醒等待队列的后继节点

### 共享模式

一个线程占有同步资源后，多个线程可以共享同步资源的状态

- 获取资源主要是通过`acquireShared()`方法，释放资源主要是通过`releaseShared()`
- `acquireShared()`
  - 通过`acquireShared()`尝试获取同步资源，返回小于0时即线程获取共享资源失败
  - 失败时通过`doAcquireShared()`方法使线程进入同步队列
- `releaseShared()`
  - 通过`tryRealease()`释放共享资源
  - 成功时执行`doReleaseShared`唤醒后继节点
    - `doReleaseShared`会在`acquireShared()`中调用来唤醒后继结点

# 数据结构

前序遍历：中左右

中序遍历：左中右

后序遍历：左右中

## 树

结点拥有的子树的数量称为结点的度

### 红黑树

- 结点不是红就是黑
- 根结点一定时黑色的
- 所有的叶子结点（NIL结点）都是黑色的
- 不能有两个连续的红色结点
- 任意结点到叶子结点的所有路径含有相同数量的黑色结点

# 集合框架

`LinkedHashMap`

- 插入顺序：先添加的在前面，后添加的在后面，修改操作不影响顺序
- 访问顺序：执行`get/put`后对应的键值对会移动到链表末尾，所以靠近链表末尾的是最近访问的，靠近头部是最久没有访问的
- 属性`accessOrder`，构造器默认为false
  - true代表访问顺序访问
  - false代表插入顺序访问

`TreeMap`

- 具备按键排序
- 可以按照自然顺序或者自定义比较器进行排序

`ArrayList`

- 实现`RandomAccess`，该接口是一个标记，表示这个类使用索引遍历比迭代器要更快（`CopyOnWriteArrayList、Stack、Vector`都实现了这个接口）
- 默认初始容量为10，数组长度上限为最大值减去8，未传入参数时默认构造出一个空数组，只有第一次添加元素时才会进行扩容到10
- 每次添加元素时会先判断是否需要扩容，需要则先扩容后再添加
- 数组扩容时，调用`grow()`方法，先将新容量设置为旧容量的1.5倍，若新容量小于可用最小容量，则将可用最小容量设置为新容量，若新容量大于数组长度的上限，则将新容量设置为数组长度上线和最大的整型数中的大值

`LinkList`

- 因为获取数据需要遍历，在此基础进行了优化，如果index小于链表长度的一半，则进行正序遍历，反之进行倒序遍历
- 当随机插入时，需要遍历链表，但是总体还是比`ArrayList`的`System.arraycopy`性能好

# 线程池

## `ThreadPoolExecutor`

- 构造器参数
  - `int corePoolSize`：核心线程数量，其中属性`allowCoreThreadTimeOut`默认为false，表示核心线程即使处于空闲状态也不会被回收
  - `int maximumPoolSize`：线程池可容纳的最大线程数量
  - `long keepAliveTime`：空闲线程等待新任务的最大等待时间
  - `TimeUnit unit`：`keepAliveTime`的时间单位
  - `BlockingQueue<Runnable> workQueue`：在任务被执行之前用于保存任务的队列
  - `ThreadFactory threadFactory`：线程池创建新线程使用的线程工厂
  - `RejectedExecutionHandler handler`：线程池的拒绝执行处理程序

- 执行流程

  - 线程池内线程数量小于`corePoolSize`时，执行提交到线程池的任务

  - 线程池内线程数量大于`corePoolSize`时，任务将进入阻塞队列，等待核心线程执行

  - 线程池内线程数量大于`corePoolSize`并且阻塞队列已满时，就会创建非核心线程

  - 当线程数量等于`maximumPoolSize`，线程池拒绝执行处理程序

    ![本地](.\images\4.jpg)
    ![网络](https://gitee.com/mulberry_devil/gitee-pages-imgs/raw/master/2022-07-12_141110.jpg)
    
    ![本地](.\images\微信截图_20220511145942.png)
    ![网络](https://gitee.com/mulberry_devil/gitee-pages-imgs/raw/master/2022-07-12_141201.png)

- 执行实例

```java
ThreadPoolExecutor executor = null;
        try {
            executor = new ThreadPoolExecutor(各参数);
            /* 预启动所有核心线程，提升线程池执行效率 */
            executor.prestartAllCoreThreads();
            executor.submit(任务);
        } finally {
            assert executor != null;
            executor.shutdown();
        }
```

# Interview And My Understand(IAMU)

## 书籍阅读PDF

[book](https://www.aliyundrive.com/s/Da2ZTPpP8Q7)    提取码: 8eh7 

## String 理解

- 常量池在堆中
- 在常量池中的存储形式为对象的引用

- `String s1 = new String("abc")`：该语句会运行时会创建两个对象，**在堆中创建两个对象，并且在常量池中存储堆中其中一个的引用，如果常量池拥有则不需在常量池中存储**，最后将堆中另外一个对象的地址赋值给`s1`
- `String s2 = s1.intern()`：该语句是将值存入常量池中，即常量池中寻找与` s1` 变量内容相同的对象引用，**发现已经存在内容相同对象`“abc”`的引用，返回该对象引用地址**，赋值给 `s2`，**如果未找到则让常量池中对应值的地址引用对应值的堆中地址**。
- `String s3 = "abc"`：该语句是**首先**在常量池中寻找是否有相同内容的对象引用，发现有，返回对象`"abc"`的引用地址，赋值给`s3`，**如果没有将首先在堆中创建该对象，然后常量池存储堆中引用对象，并返回常量池中的地址**，赋值给`s3`
- `String s4 = new String("3") + new String("3")`：该语句重点是`s4`**不会在常量池中创建引用对象，引用的是堆中的地址**

![String对象创建的理解图](.\images\v2-20f96416ff06190c2a495c0d9da45ce0_r.jpg)
![网络](https://gitee.com/mulberry_devil/gitee-pages-imgs/raw/master/2022-07-12_141222.jpg)

[参考链接](https://www.zhihu.com/question/55994121)

[参考链接](http://www.huangbin.fun/%E5%B0%8F%E5%B0%8FString%E5%AF%B9%E8%B1%A1%E5%A4%A7%E5%A4%A7%E5%A5%A5%E7%A7%98.html)

## 锁理解

![锁升级理解图](.\images\4491294-e3bcefb2bacea224.png)
![网络](https://gitee.com/mulberry_devil/gitee-pages-imgs/raw/master/2022-07-12_141250.png)

[参考链接](https://blog.csdn.net/Kirito_j/article/details/79201213)

[参考链接](https://tech.meituan.com/2018/11/15/java-lock.html)

## JVM内存结构

![JVM内存结构图](.\images\biyx8kz2je.png)
![网络](https://gitee.com/mulberry_devil/gitee-pages-imgs/raw/master/2022-07-12_141322.png)

[参考链接](https://cloud.tencent.com/developer/article/1810426)

## 公司项目知识

### excel读（EasyExcel）

- 封装Map中读：`EasyExcel.read(输入流, 监听器对象).sheet().doRead();`
  - 监听器继承`AnalysisEventListener<Map<Integer, String>>`重写`invoke（每读一行执行的方法）`和`doAfterAllAnalysed（读完最后一行执行的方法）`
- 封装对象读：`EasyExcel.read(输入流, 类, 监听器对象).sheet().doRead();`
  - 监听器继承`AnalysisEventListener<类>`重写`invoke（每读一行执行的方法）`和`doAfterAllAnalysed（读完最后一行执行的方法）`
- 封装对象写：`EasyExcel.write(输出流, 类).autoCloseStream(Boolean.FALSE).sheet().doWrite(数据);`
  - 类：excel表头
  - 数据：excel表数据

### 医院项目分析图

![本地](.\images\zsxl.png)
![网络](https://gitee.com/mulberry_devil/gitee-pages-imgs/raw/master/2022-07-12_141351.png)

## Redis

### 处理redis集群的hot key和big key

处理hot key还可以添加二级缓存

[参考链接](https://zhuanlan.zhihu.com/p/52393940)

### 发现hot key

1. 预估
2. 在客户端进行收集，缺点就是对客户端代码造成入侵。
3. 在Proxy层做收集，有些集群架构是下面这样的，Proxy可以是Twemproxy，是统一的入口。可以在Proxy层做收集上报，但是缺点很明显，并非所有的redis集群架构都有proxy。
4. 用redis自带命令
    - monitor命令，该命令可以实时抓取出redis服务器接收到的命令，然后写代码统计出热key是啥。当然，也有现成的分析工具可以给你使用，比如redis-faina。但是该命令在高并发的条件下，有内存增暴增的隐患，还会降低redis的性能。
    - hotkeys参数，redis 4.0.3提供了redis-cli的热点key发现功能，执行redis-cli时加上–hotkeys选项即可。但是该参数在执行的时候，如果key比较多，执行起来比较慢。
5. 抓包评估

### Redis和数据库的数据一致性

1. 先更新数据库，再更新缓存 -- 线程安全问题
2. 先删除缓存，再更新数据库 -- 延时双删
3. 先更新数据库，再删除缓存 -- 异步更新

[参考链接](https://blog.csdn.net/qq_32352565/article/details/124320577)
[参考链接](https://blog.csdn.net/diweikang/article/details/94406186)

### Redis哨兵机制

由一个或者多个哨兵（sentinel）对主从库进行监控的机制，哨兵之间是通过发布订阅_sentinel_:hello来进行关联的，哨兵的功能为监控、故障转移、通知。

哨兵会周期性地心跳检测，检测所有主从服务器是否正常运行。心跳检测方式为周期性向主从服务器发送PING命令，若主从服务器在规定时间内响应哨兵进程，则判断该服务器处于存活状态；若主从服务器在规定时间内没有响应哨兵进程，则哨兵进程会判定其下线。当(n/2+1)个哨兵判定下线才算真正下线。

下线时，哨兵集群会进行选举，首先哨兵会筛选掉已下线、断线状态、网络状态不好的从服务器，其次，会根据从服务器优先级、复制偏移量、运行ID方面进行排序，最终得到一个从服务器，那么该从服务器为新的主服务器。最好哨兵会向从服务器发送SLAVEOF命令来实现从服务器去复制新的主服务器。

在一般情况下，sentinel会以每10s一次的频率向被监视的主库和从库发送INFO命令，获取主库和从库的相关信息。当主库处于下线状态，或者sentinel正对主服务器进行故障转移操作时，sentinel向从服务发送INFO命令的频率修改为每秒一次。

[参考链接](https://segmentfault.com/a/1190000040436936)

## Mysql

### explain分析

[参考链接](https://blog.csdn.net/cczxcce/article/details/121440270)

### 分库分表

一般数据量达到500万或者2G时进行分表，数据库成为性能瓶颈进行分库

## 微服务

### seata

基于2PC（二阶段提交）协议实现，第一阶段为解析SQL，然后查询到修改之前的数据保存为before image，执行SQL并对相关表进行行锁，将执行后的数据保存为after image，将before image、after image插入到undo log表中，最后进行提交，提交成功时第二阶段为删除undo log记录，提交失败时第二阶段为从undo log进行回滚。一个事务会生成全局唯一XID。

[参考链接](https://blog.csdn.net/ttzommed/article/details/112989510)

## 消息队列

### 消息丢失

- 生产者

没有对写入失败的消息进行处理导致消息丢失

生产者发送消息至队列中，需要做好try-catch处理，如果队列返回写入失败等错误消息，需要重试发送。当多次发送失败需要作报警，日志记录等。

- 存储消息（队列）

消息写入缓冲区时就返回写入成功的响应，突然机器断电等因素没有写入磁盘中造成的消息丢失

队列需要控制响应的时机，单机情况下是消息刷盘后返回响应，集群多副本情况下，即发送至两个副本及以上的情况下再返回响应。

（刷盘，并不是每次接收到数据后就将数据写入到磁盘，而是会先写入缓冲区，将缓冲区的数据写入到磁盘的过程，称为刷盘。）

- 消费者

还没执行业务逻辑就返回消费成功导致的消息丢失

在执行完真正的业务逻辑之后再返回响应给队列

### 重复消息

生产者向队列写入时，由于网络延迟等原因，队列没有及时返回响应给生产者，导致生产者再发一次。消费者拿到消息消费了，再返回响应时这个消费者挂了，另一个消费者顶上，于是又拿到刚才那条消息，业务又被执行了一遍。于是消息又重复了。

使用幂等性处理重复消息，使用版本号进行前置判断或者通过数据库的约束例如唯一键或者使用全局唯一ID判断

### 消息堆积

生产者的生产速度与消费者的消费速度不匹配

需要先定位消费慢的原因，如果是bug则处理 bug ，如果是因为本身消费能力较弱，我们可以优化下消费逻辑，假如逻辑我们已经都优化了，但还是慢，那就得考虑水平扩容了，增加Topic的队列数和消费者数量，注意队列数一定要增加，不然新增加的消费者是没东西消费的。一个Topic中，一个队列只会分配给一个消费者。

### RabbitMQ

#### 如何确保消息正确地发送至RabbitMQ？

RabbitMQ使用发送方确认模式，确保消息正确地发送到RabbitMQ。
发送方确认模式：将信道设置成`confirm`模式（发送方确认模式），则所有在信道上发布的消息都会被指派一个唯一的ID。一旦消息被投递到目的队列后，或者消息被写入磁盘后（可持久化的消息），信道会发送一个`ack`给生产者（包含消息唯一ID）。如果`RabbitMQ`发生内部错误从而导致消息丢失，会发送一条`nack`（not acknowledged，未确认）消息。发送方确认模式是异步的，生产者应用程序在等待确认的同时，可以继续发送消息。当确认消息到达生产者应用程序，生产者应用程序的回调方法就会被触发来处理确认消息。

#### 消息持久化

1. 将queue的持久化标识durable设置为true,则代表是一个持久的队列
2. 发送消息的时候将deliveryMode=2

#### 死信队列

- 消息被拒绝（Basic.Reject或Basic.Nack）并且设置 requeue 参数的值为 false 
- 消息过期了 
  - 队列设置：在队列申明的时候使用 x-message-ttl 参数，单位为 毫秒
  - 单个消息设置：是设置消息属性的 expiration 参数的值，单位为 毫秒
  - 如果同时使用这2种方法，那么以过期时间小的那个数值为准 
- 队列达到最大的长度，先入队的消息会变成DL 

上述都会让消息进入死信队列，死信队列通过在队列申明时，给队列设置 x-dead-letter-exchange、x-dead-letter-routing-key参数 

## Java类加载

[参考链接](https://blog.csdn.net/m0_56383107/article/details/125687512)

## 垃圾回收

[参考链接](https://blog.csdn.net/zhhongying/article/details/125659869)

## JVM调优

[参考链接](https://blog.csdn.net/zydybaby/article/details/123809968)

## 秒杀系统设计实现

[参考链接](https://cloud.tencent.com/developer/article/1863530)

## 设计模式

### 单例模式

只允许类只有一个实例对象

1. 饿汉模式

   ```java
   /**
    * 饿汉模式
    * 项目启动时就初始化完成，所以不存在线程安全问题，缺点是如果项目没使用该实例会造成内存浪费
    */
   public class SingletonHungry {
       private static SingletonHungry instance = new SingletonHungry();
   
       private SingletonHungry() {
       }
   
       public static SingletonHungry getInstance() {
           return instance;
       }
   }
   
   /**
    * 饿汉模式使用枚举
    * 不存在线程安全问题
    */
   public enum SingletonEnum {
       INSTANCE;
   
       public void otherMethod() {
   
       }
   }
   ```

2. 懒汉模式

   ```java
   /**
    * 懒汉模式
    * 只有项目在使用的时候才会初始化
    */
   public class SingletonLazy {
   
       private SingletonLazy() {
       }
   
       /*
        * 1.
        *  这种方式会造成线程不安全的情况
        */
       private static SingletonLazy instance1;
   
       public static SingletonLazy getInstance1() {
           if (instance1 == null) {
               instance1 = new SingletonLazy();
           }
           return instance1;
       }
   
       /*
        * 2.
        *  instance2 = new SingletonLazy();
        *  分为三步：1. 给对象分配内存空间 2. 初始化对象 3. 将地址赋给instance2
        *  所以可能会产生指令重排变成 1-> 3 -> 2
        *  当多线程进入判断时，有可能因为指令重排判断instance2不为空，但是实际还没初始化对象，导致有线程获取一个不完整的对象实例
        *  所以要加volatile，利用内存屏障保证可见性
        */
       private static volatile SingletonLazy instance2;
   
       public static SingletonLazy getInstance2() {
           if (instance2 == null) { // 优化多线程下每个去获取锁的资源消耗
               synchronized (SingletonLazy.class) {
                   if (instance2 == null) { // 防止后续有线程是在等待锁的情况，当获取锁后再判断一次
                       instance2 = new SingletonLazy();
                   }
               }
           }
           return instance2;
       }
   
       /*
        * 3.
        *  外部类初次加载，会初始化静态变量、静态代码块、静态方法，但不会加载内部类和静态内部类。
        *  内部类只有在使用的时候才会被加载
        *  不存在线程安全问题
        */
       private static class SingletonInner {
           private static final SingletonLazy instance3 = new SingletonLazy();
       }
   
       public static SingletonLazy getInstance3() {
           return SingletonInner.instance3;
       }
   }
   ```

[应用场景](https://cloud.tencent.com/developer/article/2081946)

### 工厂模式

将对象实例的生成细节不对外暴露，做到生产和使用分离

1. 简单工厂模式 -> `com/caston/base_on_spring_boot/designpatterns/factory/simplefactory`

2. 工厂方法模式 -> `com/caston/base_on_spring_boot/designpatterns/factory/factorymethod`

3. 抽象工厂模式 -> `com/caston/base_on_spring_boot/designpatterns/factory/abstractfactory`

[相关链接](https://blog.csdn.net/qq_46015650/article/details/123799556)

### 建造者模式

将对象实例的创建和使用分离，更加注重方法的调用顺序，工厂模式更加注重创建，不关心方法调用顺序

`com/caston/base_on_spring_boot/designpatterns/buildermode`