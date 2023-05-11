package com.ead.authuser.controllers;

import com.ead.authuser.configs.security.JwtProvider;
import com.ead.authuser.dtos.JwtDto;
import com.ead.authuser.dtos.LoginDto;
import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.enums.RoleType;
import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.RoleModel;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.RoleService;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import javax.validation.Valid;
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

    @Autowired
    RoleService roleService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AuthenticationManager authenticationManager;

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
        RoleModel roleModel = roleService.findByRoleType(RoleType.ROLE_STUDENT)
                .orElseThrow(() -> new RuntimeException("Error: Role was not found!"));

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));//Esse método recebe a senha que foi enviada "Request Body" e encripta ela

        var userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel); //O BeanUtil transforma um objeto para outro
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.STUDENT);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.getRoles().add(roleModel);//vai definir o role e salvar na tabela

        userService.saveUser(userModel);//utiliza o novo saveUser para usar a exchange e publicar que um user foi criado

        //Gera os logs com LOG4J2
        log.debug("POST registerUser userId saved {}", userModel.getUserId());
        log.info("User saved successfully userId {}", userModel.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> authenticateUser(@Valid @RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwt(authentication);
        return ResponseEntity.ok(new JwtDto(jwt));
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
