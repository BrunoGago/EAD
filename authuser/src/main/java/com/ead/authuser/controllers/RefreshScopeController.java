package com.ead.authuser.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope //vai atualizar as propriedades gerenciadas pelo config server
public class RefreshScopeController {

    //Atributo "name" irá receber o valor da propriedade que estará definida no config server
    @Value("${authuser.refreshscope.name}")
    private String name;

    //Quando receber uma URI via get, irá retornar o valor que será obtido do config server
    @RequestMapping("/refreshscope")
    public String refreshscope() {
        return this.name;
    }
}
