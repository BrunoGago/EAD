package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.ead.authuser.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)//permite o acesso de qualquer origem a UserController
@RequestMapping("/users")//setamos a URI para utilizarmos o HTTP
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(SpecificationTemplate.UserSpec spec,
                                                       @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable,
                                                       @RequestParam(required = false) UUID courseId){
        Page<UserModel> userModelPage = null;
        if(courseId != null){
            userModelPage = userService.findAll(SpecificationTemplate.userCourseId(courseId).and(spec), pageable);
        } else {
            userModelPage = userService.findAll(spec, pageable);
        }
        /*Verifica se a lista de usuários está preenchida e, caso positivo, irá aplicar o Hateos para cada idem retornado
         *1- Ao percorrer a lista de usuários (UserModel) do banco, aplicamos os métodos de Hateos "add", "linkTo", "methodOn".
         *2- LinkTo: Mapeia qual o controller que vai ter a solicitação
         *3- MethodOn: Define qual método na classe controler vai receber o link. Obs: tendo em vista que o "getOneUser" precisa de um id
         * tive que passar o id do usuário
         *4- WithSelRel: Qualifica a relação do link com o recurso.
         */
        if(!userModelPage.isEmpty()){
            for(UserModel user : userModelPage.toList()){
                user.add(linkTo(methodOn(UserController.class).getOneUser(user.getUserId())).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(userModelPage);
    }

    //Tendo em vista que o retorno pode ser uma resposta ou o objeto UserModel, devemos user Object como tipo do ResponseEntity
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getOneUser(@PathVariable(value = "userId")UUID userId){
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(!userModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
        else{
            return ResponseEntity.status(HttpStatus.OK).body(userModelOptional.get());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "userId")UUID userId){

        log.debug("DELETE deleteUser userId received {}", userId);
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(!userModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
        else{
            userService.delete(userModelOptional.get());

            log.debug("DELETE deleteUser userId deleted {}", userId);
            log.info("User deleted success! userId {}", userId);
            return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully!");
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "userId")UUID userId,
                                             @RequestBody
                                             @Validated(UserDto.UserView.UserPut.class)
                                             @JsonView(UserDto.UserView.UserPut.class) UserDto userDto){

        log.debug("PUT updateUser userDto received {}", userDto.toString());
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(!userModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
        else{
            var userModel = userModelOptional.get();
            userModel.setFullName(userDto.getFullName());
            userModel.setPhoneNumber(userDto.getPhoneNumber());
            userModel.setCpf(userDto.getCpf());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

            userService.save(userModel);

            log.debug("PUT updateUser userId saved {}", userModel.getUserId());
            log.info("User updated successfully userId {}", userModel.getUserId());

            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable(value = "userId")UUID userId,
                                                 @RequestBody
                                                 @Validated(UserDto.UserView.PasswordPut.class)
                                                 @JsonView(UserDto.UserView.PasswordPut.class) UserDto userDto){

        log.debug("PUT updatePassword userDto received {}", userDto.toString());

        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(!userModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
        else if(!userModelOptional.get().getPassword().equals(userDto.getOldPassword())){
            log.warn("Password mismatched old password userId {}", userDto.getUserId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Mismatched old password!");
        }
        else{
            var userModel = userModelOptional.get();
            userModel.setPassword(userDto.getPassword());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

            userService.save(userModel);

            log.debug("PUT updatePassword userDto saved {}", userModel.toString());
            log.info("User password updated successfully userId {}", userModel.getUserId());

            return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully!");
        }
    }

    @PutMapping("/{userId}/image")
    public ResponseEntity<Object> updateImage(@PathVariable(value = "userId")UUID userId,
                                              @RequestBody
                                              @Validated(UserDto.UserView.ImagePut.class)
                                              @JsonView(UserDto.UserView.ImagePut.class) UserDto userDto){

        log.debug("PUT updateImage userDto received {}", userDto.toString());
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if(!userModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
        else{
            var userModel = userModelOptional.get();
            userModel.setImageUrl(userDto.getImageUrl());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

            userService.save(userModel);

            log.debug("PUT updateImage userDto saved {}", userModel.toString());
            log.info("User image updated successfully userId {}", userModel.getUserId());

            return ResponseEntity.status(HttpStatus.OK).body(userModel);
        }
    }
}