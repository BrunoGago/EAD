package com.ead.authuser.configs.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class AuthenticationJwtFilter extends OncePerRequestFilter {

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtStr = getTokenHeader(httpServletRequest);//pega o token do header usando o método declarado abaixo

            //checagem se o token não está nulo ou se ainda está válido
            if (jwtStr != null && jwtProvider.validateJwt(jwtStr)) {
                String username = jwtProvider.getUsernameJwt(jwtStr); //extraimos o username com o método declarado em Jwtprovider
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);//vai pegar o username, bater no BD com o método que criamos e ai transformar para UserDetails com o build()
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
