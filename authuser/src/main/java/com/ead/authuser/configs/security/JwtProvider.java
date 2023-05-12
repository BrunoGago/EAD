package com.ead.authuser.configs.security;

import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
@Log4j2
public class JwtProvider {

    @Value("${ead.auth.jwtSecret}")
    private String jwtSecret;
    @Value("${ead.auth.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwt(Authentication authentication){
        //Como na implementação coloquei o UUID, passei abaixo para que o JWT possa ser construido com o ID
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        //Extração de Roles e abaixo passamos para o JWT
        final String roles = userPrincipal.getAuthorities().stream()
                .map(role -> {
                        return role.getAuthority();}).collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject((userPrincipal.getUserId().toString()))
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith((SignatureAlgorithm.HS512), jwtSecret)
                .compact();
    }

    //extrai o username do Jwt, passando a chave "JwtSecret", dentro de body em subject, conforme mostrado acima
    public String getSubjectJwt(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    //feita a extração, vamos validar o token, retornando True ou False para a validação
    public boolean validateJwt(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}