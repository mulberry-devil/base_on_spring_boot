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

### 引入依赖

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

### 引入依赖

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

### 引入依赖

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

