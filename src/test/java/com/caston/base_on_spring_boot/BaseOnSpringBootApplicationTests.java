package com.caston.base_on_spring_boot;

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
}
