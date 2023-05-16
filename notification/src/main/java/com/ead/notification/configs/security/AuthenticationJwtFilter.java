package com.ead.notification.configs.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class AuthenticationJwtFilter extends OncePerRequestFilter {

    Logger log = LogManager.getLogger(JwtProvider.class);

    @Autowired
    JwtProvider jwtProvider;



    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtStr = getTokenHeader(httpServletRequest);//pega o token do header usando o método declarado abaixo

            //checagem se o token não está nulo ou se ainda está válido
            if (jwtStr != null && jwtProvider.validateJwt(jwtStr)) {
                String userId = jwtProvider.getSubjectJwt(jwtStr); //extraimos o userId com o método declarado em Jwtprovider
                String rolesStr = jwtProvider.getClaimNameJwt(jwtStr, "roles");//vai extrair as roles de dentro do token
                UserDetails userDetails = UserDetailsImpl.build(UUID.fromString(userId), rolesStr);
                //passa o UserDetails criado para autenticação
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                //feito isso, vai setar o Details, passando a request da solicitação
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                //passamos a autenticação para que o holder mantenha a autenticação válida para acessar as URIs
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set User Authentication: {}", e);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    //esse método extrai o token do Header da Requisição e passa para o método acima
    private String getTokenHeader(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }
        return null;
    }
}
