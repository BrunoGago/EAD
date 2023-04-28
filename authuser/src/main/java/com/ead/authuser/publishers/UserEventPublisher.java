package com.ead.authuser.publishers;

import com.ead.authuser.dtos.UserEventDto;
import com.ead.authuser.enums.ActionType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserEventPublisher {

    //Injeção de dependência com a já configurada RabbitTemplate
    @Autowired
    RabbitTemplate rabbitTemplate;

    //nome da exchange criada
    @Value(value = "${ead.broker.exchange.userEvent}")
    private String exchangeUserEvent;

    public void publishUserEvent(UserEventDto userEventDto, ActionType actionType){
        userEventDto.setActionType(actionType.toString());//conversão do ENUM para string;
        rabbitTemplate.convertAndSend(exchangeUserEvent, "", userEventDto);//passei routingKey vazia pois todas as filas eram escutar essa exchange
    }
}