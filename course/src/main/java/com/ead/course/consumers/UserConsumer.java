package com.ead.course.consumers;

import com.ead.course.dtos.UserEventDto;
import com.ead.course.enums.ActionType;
import com.ead.course.services.UserService;
import com.netflix.discovery.converters.Auto;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class UserConsumer {

    @Autowired
    UserService userService;

    /*
     * 1- @Queue: Deve ser passado o caminho definido no .yml do Rabbit para a fila que vamos criar
     * 2- @Exchange: Deve ser passado o caminho que a fila irá escutar, no caso, será o Exchange criado em authuser e definido no .yml de course tambem
     * Feito isso, a fila e exchange serão criadas
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${ead.broker.queue.userEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${ead.broker.exchange.userEventExchange}", type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true", durable = "true")
    ))
    public void listenUserEvent(@Payload UserEventDto userEventDto){
        //o UserModel que virá de authUser, via mensageria, será convertido de UserEventDto para UserModel de Course
        var userModel = userEventDto.convertToUserModel();

        //Vamos verificar o ENUM ActionType (que era string e usando valueOf será o ENUM) e vamos tomar ações para cada tipo de mensagem
        switch (ActionType.valueOf(userEventDto.getActionType())){
            case CREATE://como o save será o mesmo para os dois cases, agreguei a chamada
            case UPDATE:
                userService.save(userModel);
                break;
            case DELETE:
                userService.delete(userEventDto.getUserId());
                break;
        }
    }
}
