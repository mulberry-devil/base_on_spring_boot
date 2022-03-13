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

