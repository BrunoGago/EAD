package com.ead.authuser.clients;

import com.ead.authuser.dtos.CourseDto;
import com.ead.authuser.dtos.ResponsePageDto;
import com.ead.authuser.services.UtilsServices;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Component
public class CourseClient {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UtilsServices utilsServices;

    @Value("${ead.api.url.course}")
    String REQUEST_URL_COURSE;

    //@Retry(name = "retryInstance", fallbackMethod = "retryfallback") //Retry a nível de método com as definições feitas no application.yaml
    @CircuitBreaker(name = "circuitbreakerInstance") //chama as configs do application.yaml
    public Page<CourseDto> getAllCoursesByUser(UUID userId, Pageable pageable, String token){
        List<CourseDto> searchResult = null;
        String url = REQUEST_URL_COURSE + utilsServices.createUrlGetAllCoursesByUser(userId, pageable);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> requestEntity = new HttpEntity<>("parameters", headers);//será passado dentro do exchange em requestEntity, assim, teremos o token do cabeçalho na comunicação sincrona

        log.debug("Request URL: {} ", url);
        log.info("Request URL: {} ", url);
        ParameterizedTypeReference<ResponsePageDto<CourseDto>> responseType = new ParameterizedTypeReference<ResponsePageDto<CourseDto>>() {};
        ResponseEntity<ResponsePageDto<CourseDto>> result = restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType);
        searchResult = result.getBody().getContent();
        log.debug("Response Number of Elements: {} ", searchResult.size());

        log.info("Ending request /courses userId {} ", userId);
        return new PageImpl<>(searchResult);
    }

    //Os parâmetros devem ser os mesmos do método principal (vide linha 38)
    //FallBack: No caso de as tentativas do Retry excederem o limite setado, o método fallback será chamado e trará uma pagina vazia para o cliente
    public Page<CourseDto> retryfallback(UUID userId, Pageable pageable, Throwable t){
        log.error("Inside retry retryfallback, cause - {}", t.toString());
        List<CourseDto> searchResult = new ArrayList<>(); //O método retryFallBack deve retornar o mesmo que o método principal (vide linha 39)
        return new PageImpl<>(searchResult);
    }

    //Deixei sem o fallback para que o cliente veja que o server está fora
    public Page<CourseDto> circuitbreakerfallback(UUID userId, Pageable pageable, Throwable t) {
        log.error("Inside circuit breaker fallback, cause - {}", t.toString());
        List<CourseDto> searchResult = new ArrayList<>();
        return new PageImpl<>(searchResult);
    }
}
