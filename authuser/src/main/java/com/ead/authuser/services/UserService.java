package com.ead.authuser.services;

import com.ead.authuser.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
Essa classe é uma interface de Service, somente criando os métodos que devem ser implementados pela camada de serviços
que está criada no mesmo pacote como UserServiceImpl
 */

public interface UserService {

    List<UserModel> findAll();

    Optional<UserModel> findById(UUID userId);

    void delete(UserModel userModel);

    UserModel save(UserModel userModel);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<UserModel> findAll(Specification<UserModel> spec, Pageable pageable);

    UserModel saveUser(UserModel userModel);

    void deleteUser(UserModel userModel);
    UserModel updateUser(UserModel userModel);
    UserModel updatePassword(UserModel userModel);
}
