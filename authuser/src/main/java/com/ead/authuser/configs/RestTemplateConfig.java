package com.ead.authuser.configs;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    static final int TIMEOUT = 5000; //TimeOut de 5000 milissegundos (5 segundos)

    //RestTemplate: utilizado para fazer requisições externas, criando a comunicação síncrona entre os microserviços
    //O método abaixo é "reprodutor" que retorna o RestTemplate
    @LoadBalanced //utilização de balanceamento de carga do Spring Cloud.
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder
                .setConnectTimeout(Duration.ofMillis(TIMEOUT))//limite de tempo para coneção
                .setReadTimeout(Duration.ofMillis(TIMEOUT))//limite de tempo para leitura
                .build();
    }

}