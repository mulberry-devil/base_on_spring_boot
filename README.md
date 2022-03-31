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
        url: jdbc:mysql://112.74.87.145:3306/base_spring_boot?serverTimezone=UTC&characterEncoding=utf-8
        driver-class-name: com.mysql.cj.jdbc.Driver
        username: cqs_root
        password: cqs9527
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
	host: 172.23.11.200
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
     address: redis://172.23.11.200:6379
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
      			- 172.23.11.200
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

