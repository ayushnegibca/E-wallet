package org.gfg.UserService.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {


    private String SECRET_KEY = "jfhehhdgdhdhgdhgdgdgsgdgdsgghgsgdhgshhsghshs";
    private Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String createToken(String username){
        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*15))
                .setIssuedAt(new Date())
                .setSubject(username)
                .compact();
    }
}
