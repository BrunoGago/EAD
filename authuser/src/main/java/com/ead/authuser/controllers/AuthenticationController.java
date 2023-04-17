package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.time.LocalDateTime;
import java.time.ZoneId;


@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/auth")
public class AuthenticationController {

    //Utilização do LOG4J2, O abaixo foi substituido  pela anotação do LOG4J2 do lombok
    //Logger logger = LogManager.getLogger(AuthenticationController.class);

    @Autowired
    UserService userService;

    @PostMapping("/signup") //Método Post está separado para que o usuário se cadastre primeiro antes de acessar a aplicação
    public ResponseEntity<Object> registerUser(@RequestBody
                                                   @Validated(UserDto.UserView.RegistrationPost.class)
                                                   @JsonView(UserDto.UserView.RegistrationPost.class) UserDto userDto){
        //Gera os logs com LOG4J2
        log.debug("POST registerUser userDto received {}", userDto.toString());

        if(userService.existsByUsername(userDto.getUsername())){

            //Gera os logs com LOG4J2
            log.warn("Error: Username {} is already taken!",  userDto.getUsername());

            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Username is already taken!");
        }
        if(userService.existsByEmail(userDto.getEmail())){
            //Gera os logs com LOG4J2
            log.warn("Error: Email {} is already taken!", userDto.getEmail());

            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Email is already taken!");
        }

        var userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel); //O BeanUtil transforma um objeto para outro
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.STUDENT);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        userService.save(userModel);

        //Gera os logs com LOG4J2
        log.debug("POST registerUser userId saved {}", userModel.getUserId());
        log.info("User saved successfully userId {}", userModel.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }

    @GetMapping("/")
    public String index(){
        log.trace("TRACE");//quanto queremos a visualização dos LOGS de maneira mais granulardeg
        log.debug("DEBUG");//usado em ambiente de desenvolvimento, visualizando valores em variáveis etc.
        log.info("INFO");//Usado mais em produção mostrando como a aplicação está indo em cada processo, mas não tão detalhada
        log.warn("WARN");//Usado para alertas em processamento, como perda de dados, processos duplicados, conflitos etc. Mostra o alerta, mas não é considerado erro
        log.error("ERROR");//Usado para mostrar quando a aplicação tem erro, utilizado em TRY CATCH

        return"Logging Spring Boot...";
    }
}
