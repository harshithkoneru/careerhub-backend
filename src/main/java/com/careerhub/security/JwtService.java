package com.careerhub.security;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
 private final SecretKey key; private final long expiration;
 public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration-ms:86400000}") long expiration) {
   if(secret==null||secret.getBytes(StandardCharsets.UTF_8).length<32) throw new IllegalStateException("APP_JWT_SECRET must contain at least 32 characters");
   key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));this.expiration=expiration;
 }
 public String generate(String userId,String role){Instant now=Instant.now();return Jwts.builder().subject(userId).claim("role",role).issuedAt(Date.from(now)).expiration(Date.from(now.plusMillis(expiration))).signWith(key).compact();}
 public String subject(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();}
 public String role(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("role",String.class);}
}
