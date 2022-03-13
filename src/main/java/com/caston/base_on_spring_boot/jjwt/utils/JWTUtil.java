package com.caston.base_on_spring_boot.jjwt.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtil {
    private final static Long expTime = 10 * 60 * 1000L;
    private static String secretKey;

    public static void key() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretKey = Encoders.BASE64.encode(key.getEncoded());
    }

    public static String generate(String uid) {
        Map<String, String> claim = new HashMap<>();
        claim.put("UID", uid);
        Date current = new Date();
        Date expDate = new Date(current.getTime() + expTime);
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWTUtil.secretKey));
        return Jwts.builder()
                .setClaims(claim) // 自定义明文信息
                .setIssuedAt(current) // 签发时间
                .setExpiration(expDate) // 过期时间
                .signWith(key) // 指定密钥
                .compact();
    }

    public static Claims parse(String jwt) {
        Jws<Claims> jws = null;
        try {
            jws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt);
        } catch (JwtException ex) {
            ex.printStackTrace();
        }
        assert jws != null;
        return jws.getBody();
    }
}
