package com.ead.authuser.configs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) //Como é um Bean, essa será a classe de configuração global (PrePost autoriza as configurações pre e pos o security)
@EnableWebSecurity //"desliga" as configurações default do spring security
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    AuthenticationEntryPointImpl authenticationEntryPoint;

    private static final String[] AUTH_WHITELIST = {
            "/auth/**" //habilita o acesso a essa URI sem a necessidade do acesso sem Autenticação
    };

    @Bean
    public AuthenticationJwtFilter authenticationJwtFilter(){
        return new AuthenticationJwtFilter();
    }

    @Bean
    public RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_ADMIN > ROLE_INSTRUCTOR \n ROLE_INSTRUCTOR > ROLE_STUDENT \n ROLE_STUDENT > ROLE_USER";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)//quando houver uma exceção de autenticação, será chamado o método da classe que vai mostrar o erro
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//Sem estado de sessão, sempre que enviar requisção o token vai ser validado
                .and()
                .authorizeRequests()//essa linha e a de baixo dizem que qualquer requisição HTTP deve estar autenticada
                .antMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable();//desabilita o CSRF para que não haja uma requisoção de um site paralelo, evitando um attacker

        http.addFilterBefore(authenticationJwtFilter(), UsernamePasswordAuthenticationFilter.class);//vai levar em conta o filtro criado para as requisições
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }


    //Agora a autenticação vai ser via o Usuário salvo no BD, com login e senha, usando o Basic Authentication
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
