package com.ead.course.configs;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    //RestTemplate: utilizado para fazer requisições externas, criando a comunicação síncrona entre os microserviços
    //O método abaixo é "reprodutor" que retorna o RestTemplate
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }
}
