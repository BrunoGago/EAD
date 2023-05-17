package com.ead.authuser.configs.security;

import com.ead.authuser.models.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


//o UserDetails foi implementado a parte do UserModel para evitar uma sobrecarga de responsabilidade na classe
@Data
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private UUID userId;
    private String fullName;
    private String username;
    @JsonIgnore
    private String password;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;

    //Esse método vai transformar o UserModel (que tem as informações salvas no BD) para UserDetailsImpl (que é um padrão do Spring Security)
    public static UserDetailsImpl build(UserModel userModel){
        /*
         * Role implementa o GrantedAuth, então a lista vai ter as Roles autorizadas nela
         * O abaixo vai percorrer cada Role de User e vai verificar se está autorizado, como na lista recebida
         */
        List<GrantedAuthority> authorities = userModel.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());

        /*
         * Como usei o AllArgs, o abaixo seria o construtor com todos os argumentos
         */
        return new UserDetailsImpl(
                userModel.getUserId(),
                userModel.getFullName(),
                userModel.getUsername(),
                userModel.getPassword(),
                userModel.getEmail(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
